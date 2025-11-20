package com.romix.feature.feat16

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat16Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat16UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat16FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat16UserSummary
)

data class Feat16UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat16NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat16Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat16Config = Feat16Config()
) {

    fun loadSnapshot(userId: Long): Feat16NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat16NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat16UserSummary {
        return Feat16UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat16FeedItem> {
        val result = java.util.ArrayList<Feat16FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat16FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat16UiMapper {

    fun mapToUi(model: List<Feat16FeedItem>): Feat16UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat16UiModel(
            header = UiText("Feat16 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat16UiModel =
        Feat16UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat16UiModel =
        Feat16UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat16UiModel =
        Feat16UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat16Service(
    private val repository: Feat16Repository,
    private val uiMapper: Feat16UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat16UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat16UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat16UserItem1(val user: CoreUser, val label: String)
data class Feat16UserItem2(val user: CoreUser, val label: String)
data class Feat16UserItem3(val user: CoreUser, val label: String)
data class Feat16UserItem4(val user: CoreUser, val label: String)
data class Feat16UserItem5(val user: CoreUser, val label: String)
data class Feat16UserItem6(val user: CoreUser, val label: String)
data class Feat16UserItem7(val user: CoreUser, val label: String)
data class Feat16UserItem8(val user: CoreUser, val label: String)
data class Feat16UserItem9(val user: CoreUser, val label: String)
data class Feat16UserItem10(val user: CoreUser, val label: String)

data class Feat16StateBlock1(val state: Feat16UiModel, val checksum: Int)
data class Feat16StateBlock2(val state: Feat16UiModel, val checksum: Int)
data class Feat16StateBlock3(val state: Feat16UiModel, val checksum: Int)
data class Feat16StateBlock4(val state: Feat16UiModel, val checksum: Int)
data class Feat16StateBlock5(val state: Feat16UiModel, val checksum: Int)
data class Feat16StateBlock6(val state: Feat16UiModel, val checksum: Int)
data class Feat16StateBlock7(val state: Feat16UiModel, val checksum: Int)
data class Feat16StateBlock8(val state: Feat16UiModel, val checksum: Int)
data class Feat16StateBlock9(val state: Feat16UiModel, val checksum: Int)
data class Feat16StateBlock10(val state: Feat16UiModel, val checksum: Int)

fun buildFeat16UserItem(user: CoreUser, index: Int): Feat16UserItem1 {
    return Feat16UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat16StateBlock(model: Feat16UiModel): Feat16StateBlock1 {
    return Feat16StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat16UserSummary> {
    val list = java.util.ArrayList<Feat16UserSummary>(users.size)
    for (user in users) {
        list += Feat16UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat16UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat16UiModel {
    val summaries = (0 until count).map {
        Feat16UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat16UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat16UiModel> {
    val models = java.util.ArrayList<Feat16UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat16AnalyticsEvent1(val name: String, val value: String)
data class Feat16AnalyticsEvent2(val name: String, val value: String)
data class Feat16AnalyticsEvent3(val name: String, val value: String)
data class Feat16AnalyticsEvent4(val name: String, val value: String)
data class Feat16AnalyticsEvent5(val name: String, val value: String)
data class Feat16AnalyticsEvent6(val name: String, val value: String)
data class Feat16AnalyticsEvent7(val name: String, val value: String)
data class Feat16AnalyticsEvent8(val name: String, val value: String)
data class Feat16AnalyticsEvent9(val name: String, val value: String)
data class Feat16AnalyticsEvent10(val name: String, val value: String)

fun logFeat16Event1(event: Feat16AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat16Event2(event: Feat16AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat16Event3(event: Feat16AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat16Event4(event: Feat16AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat16Event5(event: Feat16AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat16Event6(event: Feat16AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat16Event7(event: Feat16AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat16Event8(event: Feat16AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat16Event9(event: Feat16AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat16Event10(event: Feat16AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat16Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat16Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat16Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat16Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat16Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat16Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat16Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat16Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat16Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat16Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat16(u: CoreUser): Feat16Projection1 =
    Feat16Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat16Projection1> {
    val list = java.util.ArrayList<Feat16Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat16(u)
    }
    return list
}
