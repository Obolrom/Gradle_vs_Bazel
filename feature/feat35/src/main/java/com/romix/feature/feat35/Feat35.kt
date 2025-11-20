package com.romix.feature.feat35

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat35Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat35UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat35FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat35UserSummary
)

data class Feat35UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat35NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat35Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat35Config = Feat35Config()
) {

    fun loadSnapshot(userId: Long): Feat35NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat35NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat35UserSummary {
        return Feat35UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat35FeedItem> {
        val result = java.util.ArrayList<Feat35FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat35FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat35UiMapper {

    fun mapToUi(model: List<Feat35FeedItem>): Feat35UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat35UiModel(
            header = UiText("Feat35 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat35UiModel =
        Feat35UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat35UiModel =
        Feat35UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat35UiModel =
        Feat35UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat35Service(
    private val repository: Feat35Repository,
    private val uiMapper: Feat35UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat35UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat35UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat35UserItem1(val user: CoreUser, val label: String)
data class Feat35UserItem2(val user: CoreUser, val label: String)
data class Feat35UserItem3(val user: CoreUser, val label: String)
data class Feat35UserItem4(val user: CoreUser, val label: String)
data class Feat35UserItem5(val user: CoreUser, val label: String)
data class Feat35UserItem6(val user: CoreUser, val label: String)
data class Feat35UserItem7(val user: CoreUser, val label: String)
data class Feat35UserItem8(val user: CoreUser, val label: String)
data class Feat35UserItem9(val user: CoreUser, val label: String)
data class Feat35UserItem10(val user: CoreUser, val label: String)

data class Feat35StateBlock1(val state: Feat35UiModel, val checksum: Int)
data class Feat35StateBlock2(val state: Feat35UiModel, val checksum: Int)
data class Feat35StateBlock3(val state: Feat35UiModel, val checksum: Int)
data class Feat35StateBlock4(val state: Feat35UiModel, val checksum: Int)
data class Feat35StateBlock5(val state: Feat35UiModel, val checksum: Int)
data class Feat35StateBlock6(val state: Feat35UiModel, val checksum: Int)
data class Feat35StateBlock7(val state: Feat35UiModel, val checksum: Int)
data class Feat35StateBlock8(val state: Feat35UiModel, val checksum: Int)
data class Feat35StateBlock9(val state: Feat35UiModel, val checksum: Int)
data class Feat35StateBlock10(val state: Feat35UiModel, val checksum: Int)

fun buildFeat35UserItem(user: CoreUser, index: Int): Feat35UserItem1 {
    return Feat35UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat35StateBlock(model: Feat35UiModel): Feat35StateBlock1 {
    return Feat35StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat35UserSummary> {
    val list = java.util.ArrayList<Feat35UserSummary>(users.size)
    for (user in users) {
        list += Feat35UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat35UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat35UiModel {
    val summaries = (0 until count).map {
        Feat35UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat35UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat35UiModel> {
    val models = java.util.ArrayList<Feat35UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat35AnalyticsEvent1(val name: String, val value: String)
data class Feat35AnalyticsEvent2(val name: String, val value: String)
data class Feat35AnalyticsEvent3(val name: String, val value: String)
data class Feat35AnalyticsEvent4(val name: String, val value: String)
data class Feat35AnalyticsEvent5(val name: String, val value: String)
data class Feat35AnalyticsEvent6(val name: String, val value: String)
data class Feat35AnalyticsEvent7(val name: String, val value: String)
data class Feat35AnalyticsEvent8(val name: String, val value: String)
data class Feat35AnalyticsEvent9(val name: String, val value: String)
data class Feat35AnalyticsEvent10(val name: String, val value: String)

fun logFeat35Event1(event: Feat35AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat35Event2(event: Feat35AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat35Event3(event: Feat35AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat35Event4(event: Feat35AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat35Event5(event: Feat35AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat35Event6(event: Feat35AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat35Event7(event: Feat35AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat35Event8(event: Feat35AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat35Event9(event: Feat35AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat35Event10(event: Feat35AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat35Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat35Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat35Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat35Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat35Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat35Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat35Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat35Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat35Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat35Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat35(u: CoreUser): Feat35Projection1 =
    Feat35Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat35Projection1> {
    val list = java.util.ArrayList<Feat35Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat35(u)
    }
    return list
}
