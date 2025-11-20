package com.romix.feature.feat335

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat335Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat335UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat335FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat335UserSummary
)

data class Feat335UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat335NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat335Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat335Config = Feat335Config()
) {

    fun loadSnapshot(userId: Long): Feat335NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat335NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat335UserSummary {
        return Feat335UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat335FeedItem> {
        val result = java.util.ArrayList<Feat335FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat335FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat335UiMapper {

    fun mapToUi(model: List<Feat335FeedItem>): Feat335UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat335UiModel(
            header = UiText("Feat335 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat335UiModel =
        Feat335UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat335UiModel =
        Feat335UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat335UiModel =
        Feat335UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat335Service(
    private val repository: Feat335Repository,
    private val uiMapper: Feat335UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat335UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat335UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat335UserItem1(val user: CoreUser, val label: String)
data class Feat335UserItem2(val user: CoreUser, val label: String)
data class Feat335UserItem3(val user: CoreUser, val label: String)
data class Feat335UserItem4(val user: CoreUser, val label: String)
data class Feat335UserItem5(val user: CoreUser, val label: String)
data class Feat335UserItem6(val user: CoreUser, val label: String)
data class Feat335UserItem7(val user: CoreUser, val label: String)
data class Feat335UserItem8(val user: CoreUser, val label: String)
data class Feat335UserItem9(val user: CoreUser, val label: String)
data class Feat335UserItem10(val user: CoreUser, val label: String)

data class Feat335StateBlock1(val state: Feat335UiModel, val checksum: Int)
data class Feat335StateBlock2(val state: Feat335UiModel, val checksum: Int)
data class Feat335StateBlock3(val state: Feat335UiModel, val checksum: Int)
data class Feat335StateBlock4(val state: Feat335UiModel, val checksum: Int)
data class Feat335StateBlock5(val state: Feat335UiModel, val checksum: Int)
data class Feat335StateBlock6(val state: Feat335UiModel, val checksum: Int)
data class Feat335StateBlock7(val state: Feat335UiModel, val checksum: Int)
data class Feat335StateBlock8(val state: Feat335UiModel, val checksum: Int)
data class Feat335StateBlock9(val state: Feat335UiModel, val checksum: Int)
data class Feat335StateBlock10(val state: Feat335UiModel, val checksum: Int)

fun buildFeat335UserItem(user: CoreUser, index: Int): Feat335UserItem1 {
    return Feat335UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat335StateBlock(model: Feat335UiModel): Feat335StateBlock1 {
    return Feat335StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat335UserSummary> {
    val list = java.util.ArrayList<Feat335UserSummary>(users.size)
    for (user in users) {
        list += Feat335UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat335UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat335UiModel {
    val summaries = (0 until count).map {
        Feat335UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat335UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat335UiModel> {
    val models = java.util.ArrayList<Feat335UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat335AnalyticsEvent1(val name: String, val value: String)
data class Feat335AnalyticsEvent2(val name: String, val value: String)
data class Feat335AnalyticsEvent3(val name: String, val value: String)
data class Feat335AnalyticsEvent4(val name: String, val value: String)
data class Feat335AnalyticsEvent5(val name: String, val value: String)
data class Feat335AnalyticsEvent6(val name: String, val value: String)
data class Feat335AnalyticsEvent7(val name: String, val value: String)
data class Feat335AnalyticsEvent8(val name: String, val value: String)
data class Feat335AnalyticsEvent9(val name: String, val value: String)
data class Feat335AnalyticsEvent10(val name: String, val value: String)

fun logFeat335Event1(event: Feat335AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat335Event2(event: Feat335AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat335Event3(event: Feat335AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat335Event4(event: Feat335AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat335Event5(event: Feat335AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat335Event6(event: Feat335AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat335Event7(event: Feat335AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat335Event8(event: Feat335AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat335Event9(event: Feat335AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat335Event10(event: Feat335AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat335Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat335Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat335Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat335Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat335Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat335Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat335Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat335Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat335Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat335Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat335(u: CoreUser): Feat335Projection1 =
    Feat335Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat335Projection1> {
    val list = java.util.ArrayList<Feat335Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat335(u)
    }
    return list
}
