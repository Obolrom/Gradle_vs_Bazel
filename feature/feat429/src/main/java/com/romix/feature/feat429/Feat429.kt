package com.romix.feature.feat429

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat429Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat429UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat429FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat429UserSummary
)

data class Feat429UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat429NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat429Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat429Config = Feat429Config()
) {

    fun loadSnapshot(userId: Long): Feat429NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat429NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat429UserSummary {
        return Feat429UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat429FeedItem> {
        val result = java.util.ArrayList<Feat429FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat429FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat429UiMapper {

    fun mapToUi(model: List<Feat429FeedItem>): Feat429UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat429UiModel(
            header = UiText("Feat429 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat429UiModel =
        Feat429UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat429UiModel =
        Feat429UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat429UiModel =
        Feat429UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat429Service(
    private val repository: Feat429Repository,
    private val uiMapper: Feat429UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat429UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat429UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat429UserItem1(val user: CoreUser, val label: String)
data class Feat429UserItem2(val user: CoreUser, val label: String)
data class Feat429UserItem3(val user: CoreUser, val label: String)
data class Feat429UserItem4(val user: CoreUser, val label: String)
data class Feat429UserItem5(val user: CoreUser, val label: String)
data class Feat429UserItem6(val user: CoreUser, val label: String)
data class Feat429UserItem7(val user: CoreUser, val label: String)
data class Feat429UserItem8(val user: CoreUser, val label: String)
data class Feat429UserItem9(val user: CoreUser, val label: String)
data class Feat429UserItem10(val user: CoreUser, val label: String)

data class Feat429StateBlock1(val state: Feat429UiModel, val checksum: Int)
data class Feat429StateBlock2(val state: Feat429UiModel, val checksum: Int)
data class Feat429StateBlock3(val state: Feat429UiModel, val checksum: Int)
data class Feat429StateBlock4(val state: Feat429UiModel, val checksum: Int)
data class Feat429StateBlock5(val state: Feat429UiModel, val checksum: Int)
data class Feat429StateBlock6(val state: Feat429UiModel, val checksum: Int)
data class Feat429StateBlock7(val state: Feat429UiModel, val checksum: Int)
data class Feat429StateBlock8(val state: Feat429UiModel, val checksum: Int)
data class Feat429StateBlock9(val state: Feat429UiModel, val checksum: Int)
data class Feat429StateBlock10(val state: Feat429UiModel, val checksum: Int)

fun buildFeat429UserItem(user: CoreUser, index: Int): Feat429UserItem1 {
    return Feat429UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat429StateBlock(model: Feat429UiModel): Feat429StateBlock1 {
    return Feat429StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat429UserSummary> {
    val list = java.util.ArrayList<Feat429UserSummary>(users.size)
    for (user in users) {
        list += Feat429UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat429UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat429UiModel {
    val summaries = (0 until count).map {
        Feat429UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat429UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat429UiModel> {
    val models = java.util.ArrayList<Feat429UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat429AnalyticsEvent1(val name: String, val value: String)
data class Feat429AnalyticsEvent2(val name: String, val value: String)
data class Feat429AnalyticsEvent3(val name: String, val value: String)
data class Feat429AnalyticsEvent4(val name: String, val value: String)
data class Feat429AnalyticsEvent5(val name: String, val value: String)
data class Feat429AnalyticsEvent6(val name: String, val value: String)
data class Feat429AnalyticsEvent7(val name: String, val value: String)
data class Feat429AnalyticsEvent8(val name: String, val value: String)
data class Feat429AnalyticsEvent9(val name: String, val value: String)
data class Feat429AnalyticsEvent10(val name: String, val value: String)

fun logFeat429Event1(event: Feat429AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat429Event2(event: Feat429AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat429Event3(event: Feat429AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat429Event4(event: Feat429AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat429Event5(event: Feat429AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat429Event6(event: Feat429AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat429Event7(event: Feat429AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat429Event8(event: Feat429AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat429Event9(event: Feat429AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat429Event10(event: Feat429AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat429Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat429Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat429Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat429Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat429Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat429Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat429Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat429Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat429Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat429Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat429(u: CoreUser): Feat429Projection1 =
    Feat429Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat429Projection1> {
    val list = java.util.ArrayList<Feat429Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat429(u)
    }
    return list
}
