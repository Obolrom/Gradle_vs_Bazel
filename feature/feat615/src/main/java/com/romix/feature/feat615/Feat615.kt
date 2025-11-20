package com.romix.feature.feat615

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat615Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat615UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat615FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat615UserSummary
)

data class Feat615UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat615NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat615Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat615Config = Feat615Config()
) {

    fun loadSnapshot(userId: Long): Feat615NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat615NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat615UserSummary {
        return Feat615UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat615FeedItem> {
        val result = java.util.ArrayList<Feat615FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat615FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat615UiMapper {

    fun mapToUi(model: List<Feat615FeedItem>): Feat615UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat615UiModel(
            header = UiText("Feat615 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat615UiModel =
        Feat615UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat615UiModel =
        Feat615UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat615UiModel =
        Feat615UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat615Service(
    private val repository: Feat615Repository,
    private val uiMapper: Feat615UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat615UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat615UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat615UserItem1(val user: CoreUser, val label: String)
data class Feat615UserItem2(val user: CoreUser, val label: String)
data class Feat615UserItem3(val user: CoreUser, val label: String)
data class Feat615UserItem4(val user: CoreUser, val label: String)
data class Feat615UserItem5(val user: CoreUser, val label: String)
data class Feat615UserItem6(val user: CoreUser, val label: String)
data class Feat615UserItem7(val user: CoreUser, val label: String)
data class Feat615UserItem8(val user: CoreUser, val label: String)
data class Feat615UserItem9(val user: CoreUser, val label: String)
data class Feat615UserItem10(val user: CoreUser, val label: String)

data class Feat615StateBlock1(val state: Feat615UiModel, val checksum: Int)
data class Feat615StateBlock2(val state: Feat615UiModel, val checksum: Int)
data class Feat615StateBlock3(val state: Feat615UiModel, val checksum: Int)
data class Feat615StateBlock4(val state: Feat615UiModel, val checksum: Int)
data class Feat615StateBlock5(val state: Feat615UiModel, val checksum: Int)
data class Feat615StateBlock6(val state: Feat615UiModel, val checksum: Int)
data class Feat615StateBlock7(val state: Feat615UiModel, val checksum: Int)
data class Feat615StateBlock8(val state: Feat615UiModel, val checksum: Int)
data class Feat615StateBlock9(val state: Feat615UiModel, val checksum: Int)
data class Feat615StateBlock10(val state: Feat615UiModel, val checksum: Int)

fun buildFeat615UserItem(user: CoreUser, index: Int): Feat615UserItem1 {
    return Feat615UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat615StateBlock(model: Feat615UiModel): Feat615StateBlock1 {
    return Feat615StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat615UserSummary> {
    val list = java.util.ArrayList<Feat615UserSummary>(users.size)
    for (user in users) {
        list += Feat615UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat615UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat615UiModel {
    val summaries = (0 until count).map {
        Feat615UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat615UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat615UiModel> {
    val models = java.util.ArrayList<Feat615UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat615AnalyticsEvent1(val name: String, val value: String)
data class Feat615AnalyticsEvent2(val name: String, val value: String)
data class Feat615AnalyticsEvent3(val name: String, val value: String)
data class Feat615AnalyticsEvent4(val name: String, val value: String)
data class Feat615AnalyticsEvent5(val name: String, val value: String)
data class Feat615AnalyticsEvent6(val name: String, val value: String)
data class Feat615AnalyticsEvent7(val name: String, val value: String)
data class Feat615AnalyticsEvent8(val name: String, val value: String)
data class Feat615AnalyticsEvent9(val name: String, val value: String)
data class Feat615AnalyticsEvent10(val name: String, val value: String)

fun logFeat615Event1(event: Feat615AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat615Event2(event: Feat615AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat615Event3(event: Feat615AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat615Event4(event: Feat615AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat615Event5(event: Feat615AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat615Event6(event: Feat615AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat615Event7(event: Feat615AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat615Event8(event: Feat615AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat615Event9(event: Feat615AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat615Event10(event: Feat615AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat615Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat615Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat615Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat615Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat615Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat615Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat615Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat615Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat615Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat615Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat615(u: CoreUser): Feat615Projection1 =
    Feat615Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat615Projection1> {
    val list = java.util.ArrayList<Feat615Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat615(u)
    }
    return list
}
