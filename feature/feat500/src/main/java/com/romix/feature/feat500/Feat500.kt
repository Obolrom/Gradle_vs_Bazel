package com.romix.feature.feat500

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat500Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat500UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat500FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat500UserSummary
)

data class Feat500UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat500NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat500Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat500Config = Feat500Config()
) {

    fun loadSnapshot(userId: Long): Feat500NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat500NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat500UserSummary {
        return Feat500UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat500FeedItem> {
        val result = java.util.ArrayList<Feat500FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat500FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat500UiMapper {

    fun mapToUi(model: List<Feat500FeedItem>): Feat500UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat500UiModel(
            header = UiText("Feat500 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat500UiModel =
        Feat500UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat500UiModel =
        Feat500UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat500UiModel =
        Feat500UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat500Service(
    private val repository: Feat500Repository,
    private val uiMapper: Feat500UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat500UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat500UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat500UserItem1(val user: CoreUser, val label: String)
data class Feat500UserItem2(val user: CoreUser, val label: String)
data class Feat500UserItem3(val user: CoreUser, val label: String)
data class Feat500UserItem4(val user: CoreUser, val label: String)
data class Feat500UserItem5(val user: CoreUser, val label: String)
data class Feat500UserItem6(val user: CoreUser, val label: String)
data class Feat500UserItem7(val user: CoreUser, val label: String)
data class Feat500UserItem8(val user: CoreUser, val label: String)
data class Feat500UserItem9(val user: CoreUser, val label: String)
data class Feat500UserItem10(val user: CoreUser, val label: String)

data class Feat500StateBlock1(val state: Feat500UiModel, val checksum: Int)
data class Feat500StateBlock2(val state: Feat500UiModel, val checksum: Int)
data class Feat500StateBlock3(val state: Feat500UiModel, val checksum: Int)
data class Feat500StateBlock4(val state: Feat500UiModel, val checksum: Int)
data class Feat500StateBlock5(val state: Feat500UiModel, val checksum: Int)
data class Feat500StateBlock6(val state: Feat500UiModel, val checksum: Int)
data class Feat500StateBlock7(val state: Feat500UiModel, val checksum: Int)
data class Feat500StateBlock8(val state: Feat500UiModel, val checksum: Int)
data class Feat500StateBlock9(val state: Feat500UiModel, val checksum: Int)
data class Feat500StateBlock10(val state: Feat500UiModel, val checksum: Int)

fun buildFeat500UserItem(user: CoreUser, index: Int): Feat500UserItem1 {
    return Feat500UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat500StateBlock(model: Feat500UiModel): Feat500StateBlock1 {
    return Feat500StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat500UserSummary> {
    val list = java.util.ArrayList<Feat500UserSummary>(users.size)
    for (user in users) {
        list += Feat500UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat500UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat500UiModel {
    val summaries = (0 until count).map {
        Feat500UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat500UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat500UiModel> {
    val models = java.util.ArrayList<Feat500UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat500AnalyticsEvent1(val name: String, val value: String)
data class Feat500AnalyticsEvent2(val name: String, val value: String)
data class Feat500AnalyticsEvent3(val name: String, val value: String)
data class Feat500AnalyticsEvent4(val name: String, val value: String)
data class Feat500AnalyticsEvent5(val name: String, val value: String)
data class Feat500AnalyticsEvent6(val name: String, val value: String)
data class Feat500AnalyticsEvent7(val name: String, val value: String)
data class Feat500AnalyticsEvent8(val name: String, val value: String)
data class Feat500AnalyticsEvent9(val name: String, val value: String)
data class Feat500AnalyticsEvent10(val name: String, val value: String)

fun logFeat500Event1(event: Feat500AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat500Event2(event: Feat500AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat500Event3(event: Feat500AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat500Event4(event: Feat500AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat500Event5(event: Feat500AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat500Event6(event: Feat500AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat500Event7(event: Feat500AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat500Event8(event: Feat500AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat500Event9(event: Feat500AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat500Event10(event: Feat500AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat500Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat500Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat500Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat500Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat500Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat500Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat500Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat500Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat500Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat500Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat500(u: CoreUser): Feat500Projection1 =
    Feat500Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat500Projection1> {
    val list = java.util.ArrayList<Feat500Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat500(u)
    }
    return list
}
