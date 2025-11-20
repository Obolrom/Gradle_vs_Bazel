package com.romix.feature.feat391

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat391Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat391UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat391FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat391UserSummary
)

data class Feat391UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat391NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat391Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat391Config = Feat391Config()
) {

    fun loadSnapshot(userId: Long): Feat391NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat391NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: ApiUserDto, posts: List<ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: CoreUser): Feat391UserSummary {
        return Feat391UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat391FeedItem> {
        val result = java.util.ArrayList<Feat391FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat391FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat391UiMapper {

    fun mapToUi(model: List<Feat391FeedItem>): Feat391UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat391UiModel(
            header = UiText("Feat391 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat391UiModel =
        Feat391UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat391UiModel =
        Feat391UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat391UiModel =
        Feat391UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat391Service(
    private val repository: Feat391Repository,
    private val uiMapper: Feat391UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat391UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): Feat391UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat391UserItem1(val user: CoreUser, val label: String)
data class Feat391UserItem2(val user: CoreUser, val label: String)
data class Feat391UserItem3(val user: CoreUser, val label: String)
data class Feat391UserItem4(val user: CoreUser, val label: String)
data class Feat391UserItem5(val user: CoreUser, val label: String)
data class Feat391UserItem6(val user: CoreUser, val label: String)
data class Feat391UserItem7(val user: CoreUser, val label: String)
data class Feat391UserItem8(val user: CoreUser, val label: String)
data class Feat391UserItem9(val user: CoreUser, val label: String)
data class Feat391UserItem10(val user: CoreUser, val label: String)

data class Feat391StateBlock1(val state: Feat391UiModel, val checksum: Int)
data class Feat391StateBlock2(val state: Feat391UiModel, val checksum: Int)
data class Feat391StateBlock3(val state: Feat391UiModel, val checksum: Int)
data class Feat391StateBlock4(val state: Feat391UiModel, val checksum: Int)
data class Feat391StateBlock5(val state: Feat391UiModel, val checksum: Int)
data class Feat391StateBlock6(val state: Feat391UiModel, val checksum: Int)
data class Feat391StateBlock7(val state: Feat391UiModel, val checksum: Int)
data class Feat391StateBlock8(val state: Feat391UiModel, val checksum: Int)
data class Feat391StateBlock9(val state: Feat391UiModel, val checksum: Int)
data class Feat391StateBlock10(val state: Feat391UiModel, val checksum: Int)

fun buildFeat391UserItem(user: CoreUser, index: Int): Feat391UserItem1 {
    return Feat391UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat391StateBlock(model: Feat391UiModel): Feat391StateBlock1 {
    return Feat391StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat391UserSummary> {
    val list = java.util.ArrayList<Feat391UserSummary>(users.size)
    for (user in users) {
        list += Feat391UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat391UserSummary>): List<UiListItem> {
    val items = java.util.ArrayList<UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): Feat391UiModel {
    val summaries = (0 until count).map {
        Feat391UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat391UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = java.util.ArrayList<CoreUser>(count)
    for (i in 0 until count) {
        list += CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<CoreUser>): List<UiText> {
    val list = java.util.ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat391UiModel> {
    val models = java.util.ArrayList<Feat391UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat391AnalyticsEvent1(val name: String, val value: String)
data class Feat391AnalyticsEvent2(val name: String, val value: String)
data class Feat391AnalyticsEvent3(val name: String, val value: String)
data class Feat391AnalyticsEvent4(val name: String, val value: String)
data class Feat391AnalyticsEvent5(val name: String, val value: String)
data class Feat391AnalyticsEvent6(val name: String, val value: String)
data class Feat391AnalyticsEvent7(val name: String, val value: String)
data class Feat391AnalyticsEvent8(val name: String, val value: String)
data class Feat391AnalyticsEvent9(val name: String, val value: String)
data class Feat391AnalyticsEvent10(val name: String, val value: String)

fun logFeat391Event1(event: Feat391AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat391Event2(event: Feat391AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat391Event3(event: Feat391AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat391Event4(event: Feat391AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat391Event5(event: Feat391AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat391Event6(event: Feat391AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat391Event7(event: Feat391AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat391Event8(event: Feat391AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat391Event9(event: Feat391AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat391Event10(event: Feat391AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat391Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat391Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat391Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat391Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat391Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat391Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat391Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat391Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat391Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat391Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat391(u: CoreUser): Feat391Projection1 =
    Feat391Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat391Projection1> {
    val list = java.util.ArrayList<Feat391Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat391(u)
    }
    return list
}
