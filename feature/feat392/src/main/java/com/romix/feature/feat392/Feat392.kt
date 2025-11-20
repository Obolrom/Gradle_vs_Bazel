package com.romix.feature.feat392

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat392Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat392UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat392FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat392UserSummary
)

data class Feat392UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat392NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat392Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat392Config = Feat392Config()
) {

    fun loadSnapshot(userId: Long): Feat392NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat392NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat392UserSummary {
        return Feat392UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat392FeedItem> {
        val result = java.util.ArrayList<Feat392FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat392FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat392UiMapper {

    fun mapToUi(model: List<Feat392FeedItem>): Feat392UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat392UiModel(
            header = UiText("Feat392 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat392UiModel =
        Feat392UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat392UiModel =
        Feat392UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat392UiModel =
        Feat392UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat392Service(
    private val repository: Feat392Repository,
    private val uiMapper: Feat392UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat392UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat392UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat392UserItem1(val user: CoreUser, val label: String)
data class Feat392UserItem2(val user: CoreUser, val label: String)
data class Feat392UserItem3(val user: CoreUser, val label: String)
data class Feat392UserItem4(val user: CoreUser, val label: String)
data class Feat392UserItem5(val user: CoreUser, val label: String)
data class Feat392UserItem6(val user: CoreUser, val label: String)
data class Feat392UserItem7(val user: CoreUser, val label: String)
data class Feat392UserItem8(val user: CoreUser, val label: String)
data class Feat392UserItem9(val user: CoreUser, val label: String)
data class Feat392UserItem10(val user: CoreUser, val label: String)

data class Feat392StateBlock1(val state: Feat392UiModel, val checksum: Int)
data class Feat392StateBlock2(val state: Feat392UiModel, val checksum: Int)
data class Feat392StateBlock3(val state: Feat392UiModel, val checksum: Int)
data class Feat392StateBlock4(val state: Feat392UiModel, val checksum: Int)
data class Feat392StateBlock5(val state: Feat392UiModel, val checksum: Int)
data class Feat392StateBlock6(val state: Feat392UiModel, val checksum: Int)
data class Feat392StateBlock7(val state: Feat392UiModel, val checksum: Int)
data class Feat392StateBlock8(val state: Feat392UiModel, val checksum: Int)
data class Feat392StateBlock9(val state: Feat392UiModel, val checksum: Int)
data class Feat392StateBlock10(val state: Feat392UiModel, val checksum: Int)

fun buildFeat392UserItem(user: CoreUser, index: Int): Feat392UserItem1 {
    return Feat392UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat392StateBlock(model: Feat392UiModel): Feat392StateBlock1 {
    return Feat392StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat392UserSummary> {
    val list = java.util.ArrayList<Feat392UserSummary>(users.size)
    for (user in users) {
        list += Feat392UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat392UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat392UiModel {
    val summaries = (0 until count).map {
        Feat392UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat392UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat392UiModel> {
    val models = java.util.ArrayList<Feat392UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat392AnalyticsEvent1(val name: String, val value: String)
data class Feat392AnalyticsEvent2(val name: String, val value: String)
data class Feat392AnalyticsEvent3(val name: String, val value: String)
data class Feat392AnalyticsEvent4(val name: String, val value: String)
data class Feat392AnalyticsEvent5(val name: String, val value: String)
data class Feat392AnalyticsEvent6(val name: String, val value: String)
data class Feat392AnalyticsEvent7(val name: String, val value: String)
data class Feat392AnalyticsEvent8(val name: String, val value: String)
data class Feat392AnalyticsEvent9(val name: String, val value: String)
data class Feat392AnalyticsEvent10(val name: String, val value: String)

fun logFeat392Event1(event: Feat392AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat392Event2(event: Feat392AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat392Event3(event: Feat392AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat392Event4(event: Feat392AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat392Event5(event: Feat392AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat392Event6(event: Feat392AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat392Event7(event: Feat392AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat392Event8(event: Feat392AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat392Event9(event: Feat392AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat392Event10(event: Feat392AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat392Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat392Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat392Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat392Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat392Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat392Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat392Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat392Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat392Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat392Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat392(u: CoreUser): Feat392Projection1 =
    Feat392Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat392Projection1> {
    val list = java.util.ArrayList<Feat392Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat392(u)
    }
    return list
}
