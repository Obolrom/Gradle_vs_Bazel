package com.romix.feature.feat76

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat76Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat76UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat76FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat76UserSummary
)

data class Feat76UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat76NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat76Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat76Config = Feat76Config()
) {

    fun loadSnapshot(userId: Long): Feat76NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat76NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat76UserSummary {
        return Feat76UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat76FeedItem> {
        val result = java.util.ArrayList<Feat76FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat76FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat76UiMapper {

    fun mapToUi(model: List<Feat76FeedItem>): Feat76UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat76UiModel(
            header = UiText("Feat76 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat76UiModel =
        Feat76UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat76UiModel =
        Feat76UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat76UiModel =
        Feat76UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat76Service(
    private val repository: Feat76Repository,
    private val uiMapper: Feat76UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat76UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat76UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat76UserItem1(val user: CoreUser, val label: String)
data class Feat76UserItem2(val user: CoreUser, val label: String)
data class Feat76UserItem3(val user: CoreUser, val label: String)
data class Feat76UserItem4(val user: CoreUser, val label: String)
data class Feat76UserItem5(val user: CoreUser, val label: String)
data class Feat76UserItem6(val user: CoreUser, val label: String)
data class Feat76UserItem7(val user: CoreUser, val label: String)
data class Feat76UserItem8(val user: CoreUser, val label: String)
data class Feat76UserItem9(val user: CoreUser, val label: String)
data class Feat76UserItem10(val user: CoreUser, val label: String)

data class Feat76StateBlock1(val state: Feat76UiModel, val checksum: Int)
data class Feat76StateBlock2(val state: Feat76UiModel, val checksum: Int)
data class Feat76StateBlock3(val state: Feat76UiModel, val checksum: Int)
data class Feat76StateBlock4(val state: Feat76UiModel, val checksum: Int)
data class Feat76StateBlock5(val state: Feat76UiModel, val checksum: Int)
data class Feat76StateBlock6(val state: Feat76UiModel, val checksum: Int)
data class Feat76StateBlock7(val state: Feat76UiModel, val checksum: Int)
data class Feat76StateBlock8(val state: Feat76UiModel, val checksum: Int)
data class Feat76StateBlock9(val state: Feat76UiModel, val checksum: Int)
data class Feat76StateBlock10(val state: Feat76UiModel, val checksum: Int)

fun buildFeat76UserItem(user: CoreUser, index: Int): Feat76UserItem1 {
    return Feat76UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat76StateBlock(model: Feat76UiModel): Feat76StateBlock1 {
    return Feat76StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat76UserSummary> {
    val list = java.util.ArrayList<Feat76UserSummary>(users.size)
    for (user in users) {
        list += Feat76UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat76UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat76UiModel {
    val summaries = (0 until count).map {
        Feat76UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat76UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat76UiModel> {
    val models = java.util.ArrayList<Feat76UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat76AnalyticsEvent1(val name: String, val value: String)
data class Feat76AnalyticsEvent2(val name: String, val value: String)
data class Feat76AnalyticsEvent3(val name: String, val value: String)
data class Feat76AnalyticsEvent4(val name: String, val value: String)
data class Feat76AnalyticsEvent5(val name: String, val value: String)
data class Feat76AnalyticsEvent6(val name: String, val value: String)
data class Feat76AnalyticsEvent7(val name: String, val value: String)
data class Feat76AnalyticsEvent8(val name: String, val value: String)
data class Feat76AnalyticsEvent9(val name: String, val value: String)
data class Feat76AnalyticsEvent10(val name: String, val value: String)

fun logFeat76Event1(event: Feat76AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat76Event2(event: Feat76AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat76Event3(event: Feat76AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat76Event4(event: Feat76AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat76Event5(event: Feat76AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat76Event6(event: Feat76AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat76Event7(event: Feat76AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat76Event8(event: Feat76AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat76Event9(event: Feat76AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat76Event10(event: Feat76AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat76Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat76Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat76Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat76Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat76Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat76Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat76Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat76Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat76Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat76Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat76(u: CoreUser): Feat76Projection1 =
    Feat76Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat76Projection1> {
    val list = java.util.ArrayList<Feat76Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat76(u)
    }
    return list
}
