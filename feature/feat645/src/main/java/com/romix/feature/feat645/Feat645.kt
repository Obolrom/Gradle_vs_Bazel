package com.romix.feature.feat645

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat645Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat645UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat645FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat645UserSummary
)

data class Feat645UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat645NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat645Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat645Config = Feat645Config()
) {

    fun loadSnapshot(userId: Long): Feat645NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat645NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat645UserSummary {
        return Feat645UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat645FeedItem> {
        val result = java.util.ArrayList<Feat645FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat645FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat645UiMapper {

    fun mapToUi(model: List<Feat645FeedItem>): Feat645UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat645UiModel(
            header = UiText("Feat645 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat645UiModel =
        Feat645UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat645UiModel =
        Feat645UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat645UiModel =
        Feat645UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat645Service(
    private val repository: Feat645Repository,
    private val uiMapper: Feat645UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat645UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat645UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat645UserItem1(val user: CoreUser, val label: String)
data class Feat645UserItem2(val user: CoreUser, val label: String)
data class Feat645UserItem3(val user: CoreUser, val label: String)
data class Feat645UserItem4(val user: CoreUser, val label: String)
data class Feat645UserItem5(val user: CoreUser, val label: String)
data class Feat645UserItem6(val user: CoreUser, val label: String)
data class Feat645UserItem7(val user: CoreUser, val label: String)
data class Feat645UserItem8(val user: CoreUser, val label: String)
data class Feat645UserItem9(val user: CoreUser, val label: String)
data class Feat645UserItem10(val user: CoreUser, val label: String)

data class Feat645StateBlock1(val state: Feat645UiModel, val checksum: Int)
data class Feat645StateBlock2(val state: Feat645UiModel, val checksum: Int)
data class Feat645StateBlock3(val state: Feat645UiModel, val checksum: Int)
data class Feat645StateBlock4(val state: Feat645UiModel, val checksum: Int)
data class Feat645StateBlock5(val state: Feat645UiModel, val checksum: Int)
data class Feat645StateBlock6(val state: Feat645UiModel, val checksum: Int)
data class Feat645StateBlock7(val state: Feat645UiModel, val checksum: Int)
data class Feat645StateBlock8(val state: Feat645UiModel, val checksum: Int)
data class Feat645StateBlock9(val state: Feat645UiModel, val checksum: Int)
data class Feat645StateBlock10(val state: Feat645UiModel, val checksum: Int)

fun buildFeat645UserItem(user: CoreUser, index: Int): Feat645UserItem1 {
    return Feat645UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat645StateBlock(model: Feat645UiModel): Feat645StateBlock1 {
    return Feat645StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat645UserSummary> {
    val list = java.util.ArrayList<Feat645UserSummary>(users.size)
    for (user in users) {
        list += Feat645UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat645UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat645UiModel {
    val summaries = (0 until count).map {
        Feat645UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat645UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat645UiModel> {
    val models = java.util.ArrayList<Feat645UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat645AnalyticsEvent1(val name: String, val value: String)
data class Feat645AnalyticsEvent2(val name: String, val value: String)
data class Feat645AnalyticsEvent3(val name: String, val value: String)
data class Feat645AnalyticsEvent4(val name: String, val value: String)
data class Feat645AnalyticsEvent5(val name: String, val value: String)
data class Feat645AnalyticsEvent6(val name: String, val value: String)
data class Feat645AnalyticsEvent7(val name: String, val value: String)
data class Feat645AnalyticsEvent8(val name: String, val value: String)
data class Feat645AnalyticsEvent9(val name: String, val value: String)
data class Feat645AnalyticsEvent10(val name: String, val value: String)

fun logFeat645Event1(event: Feat645AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat645Event2(event: Feat645AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat645Event3(event: Feat645AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat645Event4(event: Feat645AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat645Event5(event: Feat645AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat645Event6(event: Feat645AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat645Event7(event: Feat645AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat645Event8(event: Feat645AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat645Event9(event: Feat645AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat645Event10(event: Feat645AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat645Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat645Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat645Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat645Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat645Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat645Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat645Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat645Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat645Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat645Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat645(u: CoreUser): Feat645Projection1 =
    Feat645Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat645Projection1> {
    val list = java.util.ArrayList<Feat645Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat645(u)
    }
    return list
}
