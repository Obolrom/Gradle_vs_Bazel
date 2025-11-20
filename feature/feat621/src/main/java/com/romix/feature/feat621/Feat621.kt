package com.romix.feature.feat621

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat621Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat621UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat621FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat621UserSummary
)

data class Feat621UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat621NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat621Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat621Config = Feat621Config()
) {

    fun loadSnapshot(userId: Long): Feat621NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat621NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat621UserSummary {
        return Feat621UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat621FeedItem> {
        val result = java.util.ArrayList<Feat621FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat621FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat621UiMapper {

    fun mapToUi(model: List<Feat621FeedItem>): Feat621UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat621UiModel(
            header = UiText("Feat621 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat621UiModel =
        Feat621UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat621UiModel =
        Feat621UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat621UiModel =
        Feat621UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat621Service(
    private val repository: Feat621Repository,
    private val uiMapper: Feat621UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat621UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat621UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat621UserItem1(val user: CoreUser, val label: String)
data class Feat621UserItem2(val user: CoreUser, val label: String)
data class Feat621UserItem3(val user: CoreUser, val label: String)
data class Feat621UserItem4(val user: CoreUser, val label: String)
data class Feat621UserItem5(val user: CoreUser, val label: String)
data class Feat621UserItem6(val user: CoreUser, val label: String)
data class Feat621UserItem7(val user: CoreUser, val label: String)
data class Feat621UserItem8(val user: CoreUser, val label: String)
data class Feat621UserItem9(val user: CoreUser, val label: String)
data class Feat621UserItem10(val user: CoreUser, val label: String)

data class Feat621StateBlock1(val state: Feat621UiModel, val checksum: Int)
data class Feat621StateBlock2(val state: Feat621UiModel, val checksum: Int)
data class Feat621StateBlock3(val state: Feat621UiModel, val checksum: Int)
data class Feat621StateBlock4(val state: Feat621UiModel, val checksum: Int)
data class Feat621StateBlock5(val state: Feat621UiModel, val checksum: Int)
data class Feat621StateBlock6(val state: Feat621UiModel, val checksum: Int)
data class Feat621StateBlock7(val state: Feat621UiModel, val checksum: Int)
data class Feat621StateBlock8(val state: Feat621UiModel, val checksum: Int)
data class Feat621StateBlock9(val state: Feat621UiModel, val checksum: Int)
data class Feat621StateBlock10(val state: Feat621UiModel, val checksum: Int)

fun buildFeat621UserItem(user: CoreUser, index: Int): Feat621UserItem1 {
    return Feat621UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat621StateBlock(model: Feat621UiModel): Feat621StateBlock1 {
    return Feat621StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat621UserSummary> {
    val list = java.util.ArrayList<Feat621UserSummary>(users.size)
    for (user in users) {
        list += Feat621UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat621UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat621UiModel {
    val summaries = (0 until count).map {
        Feat621UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat621UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat621UiModel> {
    val models = java.util.ArrayList<Feat621UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat621AnalyticsEvent1(val name: String, val value: String)
data class Feat621AnalyticsEvent2(val name: String, val value: String)
data class Feat621AnalyticsEvent3(val name: String, val value: String)
data class Feat621AnalyticsEvent4(val name: String, val value: String)
data class Feat621AnalyticsEvent5(val name: String, val value: String)
data class Feat621AnalyticsEvent6(val name: String, val value: String)
data class Feat621AnalyticsEvent7(val name: String, val value: String)
data class Feat621AnalyticsEvent8(val name: String, val value: String)
data class Feat621AnalyticsEvent9(val name: String, val value: String)
data class Feat621AnalyticsEvent10(val name: String, val value: String)

fun logFeat621Event1(event: Feat621AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat621Event2(event: Feat621AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat621Event3(event: Feat621AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat621Event4(event: Feat621AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat621Event5(event: Feat621AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat621Event6(event: Feat621AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat621Event7(event: Feat621AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat621Event8(event: Feat621AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat621Event9(event: Feat621AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat621Event10(event: Feat621AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat621Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat621Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat621Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat621Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat621Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat621Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat621Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat621Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat621Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat621Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat621(u: CoreUser): Feat621Projection1 =
    Feat621Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat621Projection1> {
    val list = java.util.ArrayList<Feat621Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat621(u)
    }
    return list
}
