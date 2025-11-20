package com.romix.feature.feat114

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat114Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat114UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat114FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat114UserSummary
)

data class Feat114UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat114NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat114Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat114Config = Feat114Config()
) {

    fun loadSnapshot(userId: Long): Feat114NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat114NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat114UserSummary {
        return Feat114UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat114FeedItem> {
        val result = java.util.ArrayList<Feat114FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat114FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat114UiMapper {

    fun mapToUi(model: List<Feat114FeedItem>): Feat114UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat114UiModel(
            header = UiText("Feat114 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat114UiModel =
        Feat114UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat114UiModel =
        Feat114UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat114UiModel =
        Feat114UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat114Service(
    private val repository: Feat114Repository,
    private val uiMapper: Feat114UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat114UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat114UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat114UserItem1(val user: CoreUser, val label: String)
data class Feat114UserItem2(val user: CoreUser, val label: String)
data class Feat114UserItem3(val user: CoreUser, val label: String)
data class Feat114UserItem4(val user: CoreUser, val label: String)
data class Feat114UserItem5(val user: CoreUser, val label: String)
data class Feat114UserItem6(val user: CoreUser, val label: String)
data class Feat114UserItem7(val user: CoreUser, val label: String)
data class Feat114UserItem8(val user: CoreUser, val label: String)
data class Feat114UserItem9(val user: CoreUser, val label: String)
data class Feat114UserItem10(val user: CoreUser, val label: String)

data class Feat114StateBlock1(val state: Feat114UiModel, val checksum: Int)
data class Feat114StateBlock2(val state: Feat114UiModel, val checksum: Int)
data class Feat114StateBlock3(val state: Feat114UiModel, val checksum: Int)
data class Feat114StateBlock4(val state: Feat114UiModel, val checksum: Int)
data class Feat114StateBlock5(val state: Feat114UiModel, val checksum: Int)
data class Feat114StateBlock6(val state: Feat114UiModel, val checksum: Int)
data class Feat114StateBlock7(val state: Feat114UiModel, val checksum: Int)
data class Feat114StateBlock8(val state: Feat114UiModel, val checksum: Int)
data class Feat114StateBlock9(val state: Feat114UiModel, val checksum: Int)
data class Feat114StateBlock10(val state: Feat114UiModel, val checksum: Int)

fun buildFeat114UserItem(user: CoreUser, index: Int): Feat114UserItem1 {
    return Feat114UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat114StateBlock(model: Feat114UiModel): Feat114StateBlock1 {
    return Feat114StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat114UserSummary> {
    val list = java.util.ArrayList<Feat114UserSummary>(users.size)
    for (user in users) {
        list += Feat114UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat114UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat114UiModel {
    val summaries = (0 until count).map {
        Feat114UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat114UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat114UiModel> {
    val models = java.util.ArrayList<Feat114UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat114AnalyticsEvent1(val name: String, val value: String)
data class Feat114AnalyticsEvent2(val name: String, val value: String)
data class Feat114AnalyticsEvent3(val name: String, val value: String)
data class Feat114AnalyticsEvent4(val name: String, val value: String)
data class Feat114AnalyticsEvent5(val name: String, val value: String)
data class Feat114AnalyticsEvent6(val name: String, val value: String)
data class Feat114AnalyticsEvent7(val name: String, val value: String)
data class Feat114AnalyticsEvent8(val name: String, val value: String)
data class Feat114AnalyticsEvent9(val name: String, val value: String)
data class Feat114AnalyticsEvent10(val name: String, val value: String)

fun logFeat114Event1(event: Feat114AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat114Event2(event: Feat114AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat114Event3(event: Feat114AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat114Event4(event: Feat114AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat114Event5(event: Feat114AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat114Event6(event: Feat114AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat114Event7(event: Feat114AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat114Event8(event: Feat114AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat114Event9(event: Feat114AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat114Event10(event: Feat114AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat114Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat114Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat114Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat114Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat114Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat114Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat114Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat114Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat114Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat114Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat114(u: CoreUser): Feat114Projection1 =
    Feat114Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat114Projection1> {
    val list = java.util.ArrayList<Feat114Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat114(u)
    }
    return list
}
