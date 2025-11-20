package com.romix.feature.feat529

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat529Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat529UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat529FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat529UserSummary
)

data class Feat529UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat529NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat529Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat529Config = Feat529Config()
) {

    fun loadSnapshot(userId: Long): Feat529NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat529NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat529UserSummary {
        return Feat529UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat529FeedItem> {
        val result = java.util.ArrayList<Feat529FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat529FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat529UiMapper {

    fun mapToUi(model: List<Feat529FeedItem>): Feat529UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat529UiModel(
            header = UiText("Feat529 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat529UiModel =
        Feat529UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat529UiModel =
        Feat529UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat529UiModel =
        Feat529UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat529Service(
    private val repository: Feat529Repository,
    private val uiMapper: Feat529UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat529UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat529UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat529UserItem1(val user: CoreUser, val label: String)
data class Feat529UserItem2(val user: CoreUser, val label: String)
data class Feat529UserItem3(val user: CoreUser, val label: String)
data class Feat529UserItem4(val user: CoreUser, val label: String)
data class Feat529UserItem5(val user: CoreUser, val label: String)
data class Feat529UserItem6(val user: CoreUser, val label: String)
data class Feat529UserItem7(val user: CoreUser, val label: String)
data class Feat529UserItem8(val user: CoreUser, val label: String)
data class Feat529UserItem9(val user: CoreUser, val label: String)
data class Feat529UserItem10(val user: CoreUser, val label: String)

data class Feat529StateBlock1(val state: Feat529UiModel, val checksum: Int)
data class Feat529StateBlock2(val state: Feat529UiModel, val checksum: Int)
data class Feat529StateBlock3(val state: Feat529UiModel, val checksum: Int)
data class Feat529StateBlock4(val state: Feat529UiModel, val checksum: Int)
data class Feat529StateBlock5(val state: Feat529UiModel, val checksum: Int)
data class Feat529StateBlock6(val state: Feat529UiModel, val checksum: Int)
data class Feat529StateBlock7(val state: Feat529UiModel, val checksum: Int)
data class Feat529StateBlock8(val state: Feat529UiModel, val checksum: Int)
data class Feat529StateBlock9(val state: Feat529UiModel, val checksum: Int)
data class Feat529StateBlock10(val state: Feat529UiModel, val checksum: Int)

fun buildFeat529UserItem(user: CoreUser, index: Int): Feat529UserItem1 {
    return Feat529UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat529StateBlock(model: Feat529UiModel): Feat529StateBlock1 {
    return Feat529StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat529UserSummary> {
    val list = java.util.ArrayList<Feat529UserSummary>(users.size)
    for (user in users) {
        list += Feat529UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat529UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat529UiModel {
    val summaries = (0 until count).map {
        Feat529UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat529UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat529UiModel> {
    val models = java.util.ArrayList<Feat529UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat529AnalyticsEvent1(val name: String, val value: String)
data class Feat529AnalyticsEvent2(val name: String, val value: String)
data class Feat529AnalyticsEvent3(val name: String, val value: String)
data class Feat529AnalyticsEvent4(val name: String, val value: String)
data class Feat529AnalyticsEvent5(val name: String, val value: String)
data class Feat529AnalyticsEvent6(val name: String, val value: String)
data class Feat529AnalyticsEvent7(val name: String, val value: String)
data class Feat529AnalyticsEvent8(val name: String, val value: String)
data class Feat529AnalyticsEvent9(val name: String, val value: String)
data class Feat529AnalyticsEvent10(val name: String, val value: String)

fun logFeat529Event1(event: Feat529AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat529Event2(event: Feat529AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat529Event3(event: Feat529AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat529Event4(event: Feat529AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat529Event5(event: Feat529AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat529Event6(event: Feat529AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat529Event7(event: Feat529AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat529Event8(event: Feat529AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat529Event9(event: Feat529AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat529Event10(event: Feat529AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat529Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat529Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat529Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat529Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat529Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat529Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat529Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat529Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat529Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat529Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat529(u: CoreUser): Feat529Projection1 =
    Feat529Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat529Projection1> {
    val list = java.util.ArrayList<Feat529Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat529(u)
    }
    return list
}
