package com.romix.feature.feat290

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat290Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat290UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat290FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat290UserSummary
)

data class Feat290UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat290NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat290Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat290Config = Feat290Config()
) {

    fun loadSnapshot(userId: Long): Feat290NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat290NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat290UserSummary {
        return Feat290UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat290FeedItem> {
        val result = java.util.ArrayList<Feat290FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat290FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat290UiMapper {

    fun mapToUi(model: List<Feat290FeedItem>): Feat290UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat290UiModel(
            header = UiText("Feat290 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat290UiModel =
        Feat290UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat290UiModel =
        Feat290UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat290UiModel =
        Feat290UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat290Service(
    private val repository: Feat290Repository,
    private val uiMapper: Feat290UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat290UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat290UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat290UserItem1(val user: CoreUser, val label: String)
data class Feat290UserItem2(val user: CoreUser, val label: String)
data class Feat290UserItem3(val user: CoreUser, val label: String)
data class Feat290UserItem4(val user: CoreUser, val label: String)
data class Feat290UserItem5(val user: CoreUser, val label: String)
data class Feat290UserItem6(val user: CoreUser, val label: String)
data class Feat290UserItem7(val user: CoreUser, val label: String)
data class Feat290UserItem8(val user: CoreUser, val label: String)
data class Feat290UserItem9(val user: CoreUser, val label: String)
data class Feat290UserItem10(val user: CoreUser, val label: String)

data class Feat290StateBlock1(val state: Feat290UiModel, val checksum: Int)
data class Feat290StateBlock2(val state: Feat290UiModel, val checksum: Int)
data class Feat290StateBlock3(val state: Feat290UiModel, val checksum: Int)
data class Feat290StateBlock4(val state: Feat290UiModel, val checksum: Int)
data class Feat290StateBlock5(val state: Feat290UiModel, val checksum: Int)
data class Feat290StateBlock6(val state: Feat290UiModel, val checksum: Int)
data class Feat290StateBlock7(val state: Feat290UiModel, val checksum: Int)
data class Feat290StateBlock8(val state: Feat290UiModel, val checksum: Int)
data class Feat290StateBlock9(val state: Feat290UiModel, val checksum: Int)
data class Feat290StateBlock10(val state: Feat290UiModel, val checksum: Int)

fun buildFeat290UserItem(user: CoreUser, index: Int): Feat290UserItem1 {
    return Feat290UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat290StateBlock(model: Feat290UiModel): Feat290StateBlock1 {
    return Feat290StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat290UserSummary> {
    val list = java.util.ArrayList<Feat290UserSummary>(users.size)
    for (user in users) {
        list += Feat290UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat290UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat290UiModel {
    val summaries = (0 until count).map {
        Feat290UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat290UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat290UiModel> {
    val models = java.util.ArrayList<Feat290UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat290AnalyticsEvent1(val name: String, val value: String)
data class Feat290AnalyticsEvent2(val name: String, val value: String)
data class Feat290AnalyticsEvent3(val name: String, val value: String)
data class Feat290AnalyticsEvent4(val name: String, val value: String)
data class Feat290AnalyticsEvent5(val name: String, val value: String)
data class Feat290AnalyticsEvent6(val name: String, val value: String)
data class Feat290AnalyticsEvent7(val name: String, val value: String)
data class Feat290AnalyticsEvent8(val name: String, val value: String)
data class Feat290AnalyticsEvent9(val name: String, val value: String)
data class Feat290AnalyticsEvent10(val name: String, val value: String)

fun logFeat290Event1(event: Feat290AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat290Event2(event: Feat290AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat290Event3(event: Feat290AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat290Event4(event: Feat290AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat290Event5(event: Feat290AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat290Event6(event: Feat290AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat290Event7(event: Feat290AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat290Event8(event: Feat290AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat290Event9(event: Feat290AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat290Event10(event: Feat290AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat290Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat290Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat290Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat290Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat290Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat290Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat290Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat290Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat290Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat290Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat290(u: CoreUser): Feat290Projection1 =
    Feat290Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat290Projection1> {
    val list = java.util.ArrayList<Feat290Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat290(u)
    }
    return list
}
