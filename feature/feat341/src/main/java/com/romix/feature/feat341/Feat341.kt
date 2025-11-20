package com.romix.feature.feat341

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat341Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat341UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat341FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat341UserSummary
)

data class Feat341UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat341NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat341Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat341Config = Feat341Config()
) {

    fun loadSnapshot(userId: Long): Feat341NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat341NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat341UserSummary {
        return Feat341UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat341FeedItem> {
        val result = java.util.ArrayList<Feat341FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat341FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat341UiMapper {

    fun mapToUi(model: List<Feat341FeedItem>): Feat341UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat341UiModel(
            header = UiText("Feat341 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat341UiModel =
        Feat341UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat341UiModel =
        Feat341UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat341UiModel =
        Feat341UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat341Service(
    private val repository: Feat341Repository,
    private val uiMapper: Feat341UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat341UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat341UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat341UserItem1(val user: CoreUser, val label: String)
data class Feat341UserItem2(val user: CoreUser, val label: String)
data class Feat341UserItem3(val user: CoreUser, val label: String)
data class Feat341UserItem4(val user: CoreUser, val label: String)
data class Feat341UserItem5(val user: CoreUser, val label: String)
data class Feat341UserItem6(val user: CoreUser, val label: String)
data class Feat341UserItem7(val user: CoreUser, val label: String)
data class Feat341UserItem8(val user: CoreUser, val label: String)
data class Feat341UserItem9(val user: CoreUser, val label: String)
data class Feat341UserItem10(val user: CoreUser, val label: String)

data class Feat341StateBlock1(val state: Feat341UiModel, val checksum: Int)
data class Feat341StateBlock2(val state: Feat341UiModel, val checksum: Int)
data class Feat341StateBlock3(val state: Feat341UiModel, val checksum: Int)
data class Feat341StateBlock4(val state: Feat341UiModel, val checksum: Int)
data class Feat341StateBlock5(val state: Feat341UiModel, val checksum: Int)
data class Feat341StateBlock6(val state: Feat341UiModel, val checksum: Int)
data class Feat341StateBlock7(val state: Feat341UiModel, val checksum: Int)
data class Feat341StateBlock8(val state: Feat341UiModel, val checksum: Int)
data class Feat341StateBlock9(val state: Feat341UiModel, val checksum: Int)
data class Feat341StateBlock10(val state: Feat341UiModel, val checksum: Int)

fun buildFeat341UserItem(user: CoreUser, index: Int): Feat341UserItem1 {
    return Feat341UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat341StateBlock(model: Feat341UiModel): Feat341StateBlock1 {
    return Feat341StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat341UserSummary> {
    val list = java.util.ArrayList<Feat341UserSummary>(users.size)
    for (user in users) {
        list += Feat341UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat341UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat341UiModel {
    val summaries = (0 until count).map {
        Feat341UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat341UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat341UiModel> {
    val models = java.util.ArrayList<Feat341UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat341AnalyticsEvent1(val name: String, val value: String)
data class Feat341AnalyticsEvent2(val name: String, val value: String)
data class Feat341AnalyticsEvent3(val name: String, val value: String)
data class Feat341AnalyticsEvent4(val name: String, val value: String)
data class Feat341AnalyticsEvent5(val name: String, val value: String)
data class Feat341AnalyticsEvent6(val name: String, val value: String)
data class Feat341AnalyticsEvent7(val name: String, val value: String)
data class Feat341AnalyticsEvent8(val name: String, val value: String)
data class Feat341AnalyticsEvent9(val name: String, val value: String)
data class Feat341AnalyticsEvent10(val name: String, val value: String)

fun logFeat341Event1(event: Feat341AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat341Event2(event: Feat341AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat341Event3(event: Feat341AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat341Event4(event: Feat341AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat341Event5(event: Feat341AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat341Event6(event: Feat341AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat341Event7(event: Feat341AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat341Event8(event: Feat341AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat341Event9(event: Feat341AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat341Event10(event: Feat341AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat341Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat341Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat341Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat341Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat341Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat341Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat341Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat341Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat341Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat341Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat341(u: CoreUser): Feat341Projection1 =
    Feat341Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat341Projection1> {
    val list = java.util.ArrayList<Feat341Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat341(u)
    }
    return list
}
