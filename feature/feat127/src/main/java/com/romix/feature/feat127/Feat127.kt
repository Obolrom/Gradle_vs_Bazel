package com.romix.feature.feat127

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat127Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat127UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat127FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat127UserSummary
)

data class Feat127UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat127NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat127Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat127Config = Feat127Config()
) {

    fun loadSnapshot(userId: Long): Feat127NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat127NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat127UserSummary {
        return Feat127UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat127FeedItem> {
        val result = java.util.ArrayList<Feat127FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat127FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat127UiMapper {

    fun mapToUi(model: List<Feat127FeedItem>): Feat127UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat127UiModel(
            header = UiText("Feat127 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat127UiModel =
        Feat127UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat127UiModel =
        Feat127UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat127UiModel =
        Feat127UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat127Service(
    private val repository: Feat127Repository,
    private val uiMapper: Feat127UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat127UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat127UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat127UserItem1(val user: CoreUser, val label: String)
data class Feat127UserItem2(val user: CoreUser, val label: String)
data class Feat127UserItem3(val user: CoreUser, val label: String)
data class Feat127UserItem4(val user: CoreUser, val label: String)
data class Feat127UserItem5(val user: CoreUser, val label: String)
data class Feat127UserItem6(val user: CoreUser, val label: String)
data class Feat127UserItem7(val user: CoreUser, val label: String)
data class Feat127UserItem8(val user: CoreUser, val label: String)
data class Feat127UserItem9(val user: CoreUser, val label: String)
data class Feat127UserItem10(val user: CoreUser, val label: String)

data class Feat127StateBlock1(val state: Feat127UiModel, val checksum: Int)
data class Feat127StateBlock2(val state: Feat127UiModel, val checksum: Int)
data class Feat127StateBlock3(val state: Feat127UiModel, val checksum: Int)
data class Feat127StateBlock4(val state: Feat127UiModel, val checksum: Int)
data class Feat127StateBlock5(val state: Feat127UiModel, val checksum: Int)
data class Feat127StateBlock6(val state: Feat127UiModel, val checksum: Int)
data class Feat127StateBlock7(val state: Feat127UiModel, val checksum: Int)
data class Feat127StateBlock8(val state: Feat127UiModel, val checksum: Int)
data class Feat127StateBlock9(val state: Feat127UiModel, val checksum: Int)
data class Feat127StateBlock10(val state: Feat127UiModel, val checksum: Int)

fun buildFeat127UserItem(user: CoreUser, index: Int): Feat127UserItem1 {
    return Feat127UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat127StateBlock(model: Feat127UiModel): Feat127StateBlock1 {
    return Feat127StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat127UserSummary> {
    val list = java.util.ArrayList<Feat127UserSummary>(users.size)
    for (user in users) {
        list += Feat127UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat127UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat127UiModel {
    val summaries = (0 until count).map {
        Feat127UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat127UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat127UiModel> {
    val models = java.util.ArrayList<Feat127UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat127AnalyticsEvent1(val name: String, val value: String)
data class Feat127AnalyticsEvent2(val name: String, val value: String)
data class Feat127AnalyticsEvent3(val name: String, val value: String)
data class Feat127AnalyticsEvent4(val name: String, val value: String)
data class Feat127AnalyticsEvent5(val name: String, val value: String)
data class Feat127AnalyticsEvent6(val name: String, val value: String)
data class Feat127AnalyticsEvent7(val name: String, val value: String)
data class Feat127AnalyticsEvent8(val name: String, val value: String)
data class Feat127AnalyticsEvent9(val name: String, val value: String)
data class Feat127AnalyticsEvent10(val name: String, val value: String)

fun logFeat127Event1(event: Feat127AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat127Event2(event: Feat127AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat127Event3(event: Feat127AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat127Event4(event: Feat127AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat127Event5(event: Feat127AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat127Event6(event: Feat127AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat127Event7(event: Feat127AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat127Event8(event: Feat127AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat127Event9(event: Feat127AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat127Event10(event: Feat127AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat127Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat127Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat127Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat127Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat127Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat127Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat127Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat127Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat127Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat127Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat127(u: CoreUser): Feat127Projection1 =
    Feat127Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat127Projection1> {
    val list = java.util.ArrayList<Feat127Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat127(u)
    }
    return list
}
