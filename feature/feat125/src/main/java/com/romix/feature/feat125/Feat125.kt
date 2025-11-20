package com.romix.feature.feat125

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat125Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat125UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat125FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat125UserSummary
)

data class Feat125UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat125NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat125Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat125Config = Feat125Config()
) {

    fun loadSnapshot(userId: Long): Feat125NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat125NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat125UserSummary {
        return Feat125UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat125FeedItem> {
        val result = java.util.ArrayList<Feat125FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat125FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat125UiMapper {

    fun mapToUi(model: List<Feat125FeedItem>): Feat125UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat125UiModel(
            header = UiText("Feat125 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat125UiModel =
        Feat125UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat125UiModel =
        Feat125UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat125UiModel =
        Feat125UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat125Service(
    private val repository: Feat125Repository,
    private val uiMapper: Feat125UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat125UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat125UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat125UserItem1(val user: CoreUser, val label: String)
data class Feat125UserItem2(val user: CoreUser, val label: String)
data class Feat125UserItem3(val user: CoreUser, val label: String)
data class Feat125UserItem4(val user: CoreUser, val label: String)
data class Feat125UserItem5(val user: CoreUser, val label: String)
data class Feat125UserItem6(val user: CoreUser, val label: String)
data class Feat125UserItem7(val user: CoreUser, val label: String)
data class Feat125UserItem8(val user: CoreUser, val label: String)
data class Feat125UserItem9(val user: CoreUser, val label: String)
data class Feat125UserItem10(val user: CoreUser, val label: String)

data class Feat125StateBlock1(val state: Feat125UiModel, val checksum: Int)
data class Feat125StateBlock2(val state: Feat125UiModel, val checksum: Int)
data class Feat125StateBlock3(val state: Feat125UiModel, val checksum: Int)
data class Feat125StateBlock4(val state: Feat125UiModel, val checksum: Int)
data class Feat125StateBlock5(val state: Feat125UiModel, val checksum: Int)
data class Feat125StateBlock6(val state: Feat125UiModel, val checksum: Int)
data class Feat125StateBlock7(val state: Feat125UiModel, val checksum: Int)
data class Feat125StateBlock8(val state: Feat125UiModel, val checksum: Int)
data class Feat125StateBlock9(val state: Feat125UiModel, val checksum: Int)
data class Feat125StateBlock10(val state: Feat125UiModel, val checksum: Int)

fun buildFeat125UserItem(user: CoreUser, index: Int): Feat125UserItem1 {
    return Feat125UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat125StateBlock(model: Feat125UiModel): Feat125StateBlock1 {
    return Feat125StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat125UserSummary> {
    val list = java.util.ArrayList<Feat125UserSummary>(users.size)
    for (user in users) {
        list += Feat125UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat125UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat125UiModel {
    val summaries = (0 until count).map {
        Feat125UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat125UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat125UiModel> {
    val models = java.util.ArrayList<Feat125UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat125AnalyticsEvent1(val name: String, val value: String)
data class Feat125AnalyticsEvent2(val name: String, val value: String)
data class Feat125AnalyticsEvent3(val name: String, val value: String)
data class Feat125AnalyticsEvent4(val name: String, val value: String)
data class Feat125AnalyticsEvent5(val name: String, val value: String)
data class Feat125AnalyticsEvent6(val name: String, val value: String)
data class Feat125AnalyticsEvent7(val name: String, val value: String)
data class Feat125AnalyticsEvent8(val name: String, val value: String)
data class Feat125AnalyticsEvent9(val name: String, val value: String)
data class Feat125AnalyticsEvent10(val name: String, val value: String)

fun logFeat125Event1(event: Feat125AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat125Event2(event: Feat125AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat125Event3(event: Feat125AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat125Event4(event: Feat125AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat125Event5(event: Feat125AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat125Event6(event: Feat125AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat125Event7(event: Feat125AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat125Event8(event: Feat125AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat125Event9(event: Feat125AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat125Event10(event: Feat125AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat125Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat125Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat125Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat125Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat125Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat125Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat125Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat125Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat125Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat125Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat125(u: CoreUser): Feat125Projection1 =
    Feat125Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat125Projection1> {
    val list = java.util.ArrayList<Feat125Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat125(u)
    }
    return list
}
