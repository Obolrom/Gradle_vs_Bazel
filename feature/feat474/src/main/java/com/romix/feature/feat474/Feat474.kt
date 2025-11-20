package com.romix.feature.feat474

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat474Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat474UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat474FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat474UserSummary
)

data class Feat474UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat474NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat474Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat474Config = Feat474Config()
) {

    fun loadSnapshot(userId: Long): Feat474NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat474NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat474UserSummary {
        return Feat474UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat474FeedItem> {
        val result = java.util.ArrayList<Feat474FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat474FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat474UiMapper {

    fun mapToUi(model: List<Feat474FeedItem>): Feat474UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat474UiModel(
            header = UiText("Feat474 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat474UiModel =
        Feat474UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat474UiModel =
        Feat474UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat474UiModel =
        Feat474UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat474Service(
    private val repository: Feat474Repository,
    private val uiMapper: Feat474UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat474UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat474UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat474UserItem1(val user: CoreUser, val label: String)
data class Feat474UserItem2(val user: CoreUser, val label: String)
data class Feat474UserItem3(val user: CoreUser, val label: String)
data class Feat474UserItem4(val user: CoreUser, val label: String)
data class Feat474UserItem5(val user: CoreUser, val label: String)
data class Feat474UserItem6(val user: CoreUser, val label: String)
data class Feat474UserItem7(val user: CoreUser, val label: String)
data class Feat474UserItem8(val user: CoreUser, val label: String)
data class Feat474UserItem9(val user: CoreUser, val label: String)
data class Feat474UserItem10(val user: CoreUser, val label: String)

data class Feat474StateBlock1(val state: Feat474UiModel, val checksum: Int)
data class Feat474StateBlock2(val state: Feat474UiModel, val checksum: Int)
data class Feat474StateBlock3(val state: Feat474UiModel, val checksum: Int)
data class Feat474StateBlock4(val state: Feat474UiModel, val checksum: Int)
data class Feat474StateBlock5(val state: Feat474UiModel, val checksum: Int)
data class Feat474StateBlock6(val state: Feat474UiModel, val checksum: Int)
data class Feat474StateBlock7(val state: Feat474UiModel, val checksum: Int)
data class Feat474StateBlock8(val state: Feat474UiModel, val checksum: Int)
data class Feat474StateBlock9(val state: Feat474UiModel, val checksum: Int)
data class Feat474StateBlock10(val state: Feat474UiModel, val checksum: Int)

fun buildFeat474UserItem(user: CoreUser, index: Int): Feat474UserItem1 {
    return Feat474UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat474StateBlock(model: Feat474UiModel): Feat474StateBlock1 {
    return Feat474StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat474UserSummary> {
    val list = java.util.ArrayList<Feat474UserSummary>(users.size)
    for (user in users) {
        list += Feat474UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat474UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat474UiModel {
    val summaries = (0 until count).map {
        Feat474UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat474UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat474UiModel> {
    val models = java.util.ArrayList<Feat474UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat474AnalyticsEvent1(val name: String, val value: String)
data class Feat474AnalyticsEvent2(val name: String, val value: String)
data class Feat474AnalyticsEvent3(val name: String, val value: String)
data class Feat474AnalyticsEvent4(val name: String, val value: String)
data class Feat474AnalyticsEvent5(val name: String, val value: String)
data class Feat474AnalyticsEvent6(val name: String, val value: String)
data class Feat474AnalyticsEvent7(val name: String, val value: String)
data class Feat474AnalyticsEvent8(val name: String, val value: String)
data class Feat474AnalyticsEvent9(val name: String, val value: String)
data class Feat474AnalyticsEvent10(val name: String, val value: String)

fun logFeat474Event1(event: Feat474AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat474Event2(event: Feat474AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat474Event3(event: Feat474AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat474Event4(event: Feat474AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat474Event5(event: Feat474AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat474Event6(event: Feat474AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat474Event7(event: Feat474AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat474Event8(event: Feat474AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat474Event9(event: Feat474AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat474Event10(event: Feat474AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat474Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat474Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat474Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat474Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat474Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat474Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat474Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat474Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat474Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat474Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat474(u: CoreUser): Feat474Projection1 =
    Feat474Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat474Projection1> {
    val list = java.util.ArrayList<Feat474Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat474(u)
    }
    return list
}
