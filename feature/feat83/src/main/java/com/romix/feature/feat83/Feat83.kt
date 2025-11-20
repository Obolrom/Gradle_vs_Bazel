package com.romix.feature.feat83

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat83Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat83UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat83FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat83UserSummary
)

data class Feat83UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat83NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat83Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat83Config = Feat83Config()
) {

    fun loadSnapshot(userId: Long): Feat83NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat83NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat83UserSummary {
        return Feat83UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat83FeedItem> {
        val result = java.util.ArrayList<Feat83FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat83FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat83UiMapper {

    fun mapToUi(model: List<Feat83FeedItem>): Feat83UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat83UiModel(
            header = UiText("Feat83 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat83UiModel =
        Feat83UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat83UiModel =
        Feat83UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat83UiModel =
        Feat83UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat83Service(
    private val repository: Feat83Repository,
    private val uiMapper: Feat83UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat83UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat83UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat83UserItem1(val user: CoreUser, val label: String)
data class Feat83UserItem2(val user: CoreUser, val label: String)
data class Feat83UserItem3(val user: CoreUser, val label: String)
data class Feat83UserItem4(val user: CoreUser, val label: String)
data class Feat83UserItem5(val user: CoreUser, val label: String)
data class Feat83UserItem6(val user: CoreUser, val label: String)
data class Feat83UserItem7(val user: CoreUser, val label: String)
data class Feat83UserItem8(val user: CoreUser, val label: String)
data class Feat83UserItem9(val user: CoreUser, val label: String)
data class Feat83UserItem10(val user: CoreUser, val label: String)

data class Feat83StateBlock1(val state: Feat83UiModel, val checksum: Int)
data class Feat83StateBlock2(val state: Feat83UiModel, val checksum: Int)
data class Feat83StateBlock3(val state: Feat83UiModel, val checksum: Int)
data class Feat83StateBlock4(val state: Feat83UiModel, val checksum: Int)
data class Feat83StateBlock5(val state: Feat83UiModel, val checksum: Int)
data class Feat83StateBlock6(val state: Feat83UiModel, val checksum: Int)
data class Feat83StateBlock7(val state: Feat83UiModel, val checksum: Int)
data class Feat83StateBlock8(val state: Feat83UiModel, val checksum: Int)
data class Feat83StateBlock9(val state: Feat83UiModel, val checksum: Int)
data class Feat83StateBlock10(val state: Feat83UiModel, val checksum: Int)

fun buildFeat83UserItem(user: CoreUser, index: Int): Feat83UserItem1 {
    return Feat83UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat83StateBlock(model: Feat83UiModel): Feat83StateBlock1 {
    return Feat83StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat83UserSummary> {
    val list = java.util.ArrayList<Feat83UserSummary>(users.size)
    for (user in users) {
        list += Feat83UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat83UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat83UiModel {
    val summaries = (0 until count).map {
        Feat83UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat83UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat83UiModel> {
    val models = java.util.ArrayList<Feat83UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat83AnalyticsEvent1(val name: String, val value: String)
data class Feat83AnalyticsEvent2(val name: String, val value: String)
data class Feat83AnalyticsEvent3(val name: String, val value: String)
data class Feat83AnalyticsEvent4(val name: String, val value: String)
data class Feat83AnalyticsEvent5(val name: String, val value: String)
data class Feat83AnalyticsEvent6(val name: String, val value: String)
data class Feat83AnalyticsEvent7(val name: String, val value: String)
data class Feat83AnalyticsEvent8(val name: String, val value: String)
data class Feat83AnalyticsEvent9(val name: String, val value: String)
data class Feat83AnalyticsEvent10(val name: String, val value: String)

fun logFeat83Event1(event: Feat83AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat83Event2(event: Feat83AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat83Event3(event: Feat83AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat83Event4(event: Feat83AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat83Event5(event: Feat83AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat83Event6(event: Feat83AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat83Event7(event: Feat83AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat83Event8(event: Feat83AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat83Event9(event: Feat83AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat83Event10(event: Feat83AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat83Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat83Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat83Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat83Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat83Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat83Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat83Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat83Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat83Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat83Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat83(u: CoreUser): Feat83Projection1 =
    Feat83Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat83Projection1> {
    val list = java.util.ArrayList<Feat83Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat83(u)
    }
    return list
}
