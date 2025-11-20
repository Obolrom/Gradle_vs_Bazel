package com.romix.feature.feat244

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat244Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat244UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat244FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat244UserSummary
)

data class Feat244UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat244NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat244Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat244Config = Feat244Config()
) {

    fun loadSnapshot(userId: Long): Feat244NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat244NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat244UserSummary {
        return Feat244UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat244FeedItem> {
        val result = java.util.ArrayList<Feat244FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat244FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat244UiMapper {

    fun mapToUi(model: List<Feat244FeedItem>): Feat244UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat244UiModel(
            header = UiText("Feat244 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat244UiModel =
        Feat244UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat244UiModel =
        Feat244UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat244UiModel =
        Feat244UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat244Service(
    private val repository: Feat244Repository,
    private val uiMapper: Feat244UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat244UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat244UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat244UserItem1(val user: CoreUser, val label: String)
data class Feat244UserItem2(val user: CoreUser, val label: String)
data class Feat244UserItem3(val user: CoreUser, val label: String)
data class Feat244UserItem4(val user: CoreUser, val label: String)
data class Feat244UserItem5(val user: CoreUser, val label: String)
data class Feat244UserItem6(val user: CoreUser, val label: String)
data class Feat244UserItem7(val user: CoreUser, val label: String)
data class Feat244UserItem8(val user: CoreUser, val label: String)
data class Feat244UserItem9(val user: CoreUser, val label: String)
data class Feat244UserItem10(val user: CoreUser, val label: String)

data class Feat244StateBlock1(val state: Feat244UiModel, val checksum: Int)
data class Feat244StateBlock2(val state: Feat244UiModel, val checksum: Int)
data class Feat244StateBlock3(val state: Feat244UiModel, val checksum: Int)
data class Feat244StateBlock4(val state: Feat244UiModel, val checksum: Int)
data class Feat244StateBlock5(val state: Feat244UiModel, val checksum: Int)
data class Feat244StateBlock6(val state: Feat244UiModel, val checksum: Int)
data class Feat244StateBlock7(val state: Feat244UiModel, val checksum: Int)
data class Feat244StateBlock8(val state: Feat244UiModel, val checksum: Int)
data class Feat244StateBlock9(val state: Feat244UiModel, val checksum: Int)
data class Feat244StateBlock10(val state: Feat244UiModel, val checksum: Int)

fun buildFeat244UserItem(user: CoreUser, index: Int): Feat244UserItem1 {
    return Feat244UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat244StateBlock(model: Feat244UiModel): Feat244StateBlock1 {
    return Feat244StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat244UserSummary> {
    val list = java.util.ArrayList<Feat244UserSummary>(users.size)
    for (user in users) {
        list += Feat244UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat244UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat244UiModel {
    val summaries = (0 until count).map {
        Feat244UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat244UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat244UiModel> {
    val models = java.util.ArrayList<Feat244UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat244AnalyticsEvent1(val name: String, val value: String)
data class Feat244AnalyticsEvent2(val name: String, val value: String)
data class Feat244AnalyticsEvent3(val name: String, val value: String)
data class Feat244AnalyticsEvent4(val name: String, val value: String)
data class Feat244AnalyticsEvent5(val name: String, val value: String)
data class Feat244AnalyticsEvent6(val name: String, val value: String)
data class Feat244AnalyticsEvent7(val name: String, val value: String)
data class Feat244AnalyticsEvent8(val name: String, val value: String)
data class Feat244AnalyticsEvent9(val name: String, val value: String)
data class Feat244AnalyticsEvent10(val name: String, val value: String)

fun logFeat244Event1(event: Feat244AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat244Event2(event: Feat244AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat244Event3(event: Feat244AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat244Event4(event: Feat244AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat244Event5(event: Feat244AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat244Event6(event: Feat244AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat244Event7(event: Feat244AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat244Event8(event: Feat244AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat244Event9(event: Feat244AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat244Event10(event: Feat244AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat244Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat244Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat244Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat244Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat244Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat244Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat244Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat244Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat244Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat244Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat244(u: CoreUser): Feat244Projection1 =
    Feat244Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat244Projection1> {
    val list = java.util.ArrayList<Feat244Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat244(u)
    }
    return list
}
