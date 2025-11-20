package com.romix.feature.feat565

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat565Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat565UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat565FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat565UserSummary
)

data class Feat565UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat565NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat565Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat565Config = Feat565Config()
) {

    fun loadSnapshot(userId: Long): Feat565NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat565NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat565UserSummary {
        return Feat565UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat565FeedItem> {
        val result = java.util.ArrayList<Feat565FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat565FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat565UiMapper {

    fun mapToUi(model: List<Feat565FeedItem>): Feat565UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat565UiModel(
            header = UiText("Feat565 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat565UiModel =
        Feat565UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat565UiModel =
        Feat565UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat565UiModel =
        Feat565UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat565Service(
    private val repository: Feat565Repository,
    private val uiMapper: Feat565UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat565UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat565UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat565UserItem1(val user: CoreUser, val label: String)
data class Feat565UserItem2(val user: CoreUser, val label: String)
data class Feat565UserItem3(val user: CoreUser, val label: String)
data class Feat565UserItem4(val user: CoreUser, val label: String)
data class Feat565UserItem5(val user: CoreUser, val label: String)
data class Feat565UserItem6(val user: CoreUser, val label: String)
data class Feat565UserItem7(val user: CoreUser, val label: String)
data class Feat565UserItem8(val user: CoreUser, val label: String)
data class Feat565UserItem9(val user: CoreUser, val label: String)
data class Feat565UserItem10(val user: CoreUser, val label: String)

data class Feat565StateBlock1(val state: Feat565UiModel, val checksum: Int)
data class Feat565StateBlock2(val state: Feat565UiModel, val checksum: Int)
data class Feat565StateBlock3(val state: Feat565UiModel, val checksum: Int)
data class Feat565StateBlock4(val state: Feat565UiModel, val checksum: Int)
data class Feat565StateBlock5(val state: Feat565UiModel, val checksum: Int)
data class Feat565StateBlock6(val state: Feat565UiModel, val checksum: Int)
data class Feat565StateBlock7(val state: Feat565UiModel, val checksum: Int)
data class Feat565StateBlock8(val state: Feat565UiModel, val checksum: Int)
data class Feat565StateBlock9(val state: Feat565UiModel, val checksum: Int)
data class Feat565StateBlock10(val state: Feat565UiModel, val checksum: Int)

fun buildFeat565UserItem(user: CoreUser, index: Int): Feat565UserItem1 {
    return Feat565UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat565StateBlock(model: Feat565UiModel): Feat565StateBlock1 {
    return Feat565StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat565UserSummary> {
    val list = java.util.ArrayList<Feat565UserSummary>(users.size)
    for (user in users) {
        list += Feat565UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat565UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat565UiModel {
    val summaries = (0 until count).map {
        Feat565UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat565UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat565UiModel> {
    val models = java.util.ArrayList<Feat565UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat565AnalyticsEvent1(val name: String, val value: String)
data class Feat565AnalyticsEvent2(val name: String, val value: String)
data class Feat565AnalyticsEvent3(val name: String, val value: String)
data class Feat565AnalyticsEvent4(val name: String, val value: String)
data class Feat565AnalyticsEvent5(val name: String, val value: String)
data class Feat565AnalyticsEvent6(val name: String, val value: String)
data class Feat565AnalyticsEvent7(val name: String, val value: String)
data class Feat565AnalyticsEvent8(val name: String, val value: String)
data class Feat565AnalyticsEvent9(val name: String, val value: String)
data class Feat565AnalyticsEvent10(val name: String, val value: String)

fun logFeat565Event1(event: Feat565AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat565Event2(event: Feat565AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat565Event3(event: Feat565AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat565Event4(event: Feat565AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat565Event5(event: Feat565AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat565Event6(event: Feat565AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat565Event7(event: Feat565AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat565Event8(event: Feat565AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat565Event9(event: Feat565AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat565Event10(event: Feat565AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat565Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat565Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat565Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat565Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat565Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat565Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat565Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat565Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat565Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat565Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat565(u: CoreUser): Feat565Projection1 =
    Feat565Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat565Projection1> {
    val list = java.util.ArrayList<Feat565Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat565(u)
    }
    return list
}
