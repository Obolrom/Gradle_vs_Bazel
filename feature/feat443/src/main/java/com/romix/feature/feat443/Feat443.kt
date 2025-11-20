package com.romix.feature.feat443

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat443Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat443UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat443FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat443UserSummary
)

data class Feat443UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat443NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat443Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat443Config = Feat443Config()
) {

    fun loadSnapshot(userId: Long): Feat443NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat443NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat443UserSummary {
        return Feat443UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat443FeedItem> {
        val result = java.util.ArrayList<Feat443FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat443FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat443UiMapper {

    fun mapToUi(model: List<Feat443FeedItem>): Feat443UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat443UiModel(
            header = UiText("Feat443 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat443UiModel =
        Feat443UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat443UiModel =
        Feat443UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat443UiModel =
        Feat443UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat443Service(
    private val repository: Feat443Repository,
    private val uiMapper: Feat443UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat443UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat443UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat443UserItem1(val user: CoreUser, val label: String)
data class Feat443UserItem2(val user: CoreUser, val label: String)
data class Feat443UserItem3(val user: CoreUser, val label: String)
data class Feat443UserItem4(val user: CoreUser, val label: String)
data class Feat443UserItem5(val user: CoreUser, val label: String)
data class Feat443UserItem6(val user: CoreUser, val label: String)
data class Feat443UserItem7(val user: CoreUser, val label: String)
data class Feat443UserItem8(val user: CoreUser, val label: String)
data class Feat443UserItem9(val user: CoreUser, val label: String)
data class Feat443UserItem10(val user: CoreUser, val label: String)

data class Feat443StateBlock1(val state: Feat443UiModel, val checksum: Int)
data class Feat443StateBlock2(val state: Feat443UiModel, val checksum: Int)
data class Feat443StateBlock3(val state: Feat443UiModel, val checksum: Int)
data class Feat443StateBlock4(val state: Feat443UiModel, val checksum: Int)
data class Feat443StateBlock5(val state: Feat443UiModel, val checksum: Int)
data class Feat443StateBlock6(val state: Feat443UiModel, val checksum: Int)
data class Feat443StateBlock7(val state: Feat443UiModel, val checksum: Int)
data class Feat443StateBlock8(val state: Feat443UiModel, val checksum: Int)
data class Feat443StateBlock9(val state: Feat443UiModel, val checksum: Int)
data class Feat443StateBlock10(val state: Feat443UiModel, val checksum: Int)

fun buildFeat443UserItem(user: CoreUser, index: Int): Feat443UserItem1 {
    return Feat443UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat443StateBlock(model: Feat443UiModel): Feat443StateBlock1 {
    return Feat443StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat443UserSummary> {
    val list = java.util.ArrayList<Feat443UserSummary>(users.size)
    for (user in users) {
        list += Feat443UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat443UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat443UiModel {
    val summaries = (0 until count).map {
        Feat443UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat443UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat443UiModel> {
    val models = java.util.ArrayList<Feat443UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat443AnalyticsEvent1(val name: String, val value: String)
data class Feat443AnalyticsEvent2(val name: String, val value: String)
data class Feat443AnalyticsEvent3(val name: String, val value: String)
data class Feat443AnalyticsEvent4(val name: String, val value: String)
data class Feat443AnalyticsEvent5(val name: String, val value: String)
data class Feat443AnalyticsEvent6(val name: String, val value: String)
data class Feat443AnalyticsEvent7(val name: String, val value: String)
data class Feat443AnalyticsEvent8(val name: String, val value: String)
data class Feat443AnalyticsEvent9(val name: String, val value: String)
data class Feat443AnalyticsEvent10(val name: String, val value: String)

fun logFeat443Event1(event: Feat443AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat443Event2(event: Feat443AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat443Event3(event: Feat443AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat443Event4(event: Feat443AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat443Event5(event: Feat443AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat443Event6(event: Feat443AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat443Event7(event: Feat443AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat443Event8(event: Feat443AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat443Event9(event: Feat443AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat443Event10(event: Feat443AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat443Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat443Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat443Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat443Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat443Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat443Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat443Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat443Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat443Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat443Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat443(u: CoreUser): Feat443Projection1 =
    Feat443Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat443Projection1> {
    val list = java.util.ArrayList<Feat443Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat443(u)
    }
    return list
}
