package com.romix.feature.feat286

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat286Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat286UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat286FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat286UserSummary
)

data class Feat286UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat286NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat286Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat286Config = Feat286Config()
) {

    fun loadSnapshot(userId: Long): Feat286NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat286NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat286UserSummary {
        return Feat286UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat286FeedItem> {
        val result = java.util.ArrayList<Feat286FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat286FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat286UiMapper {

    fun mapToUi(model: List<Feat286FeedItem>): Feat286UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat286UiModel(
            header = UiText("Feat286 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat286UiModel =
        Feat286UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat286UiModel =
        Feat286UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat286UiModel =
        Feat286UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat286Service(
    private val repository: Feat286Repository,
    private val uiMapper: Feat286UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat286UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat286UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat286UserItem1(val user: CoreUser, val label: String)
data class Feat286UserItem2(val user: CoreUser, val label: String)
data class Feat286UserItem3(val user: CoreUser, val label: String)
data class Feat286UserItem4(val user: CoreUser, val label: String)
data class Feat286UserItem5(val user: CoreUser, val label: String)
data class Feat286UserItem6(val user: CoreUser, val label: String)
data class Feat286UserItem7(val user: CoreUser, val label: String)
data class Feat286UserItem8(val user: CoreUser, val label: String)
data class Feat286UserItem9(val user: CoreUser, val label: String)
data class Feat286UserItem10(val user: CoreUser, val label: String)

data class Feat286StateBlock1(val state: Feat286UiModel, val checksum: Int)
data class Feat286StateBlock2(val state: Feat286UiModel, val checksum: Int)
data class Feat286StateBlock3(val state: Feat286UiModel, val checksum: Int)
data class Feat286StateBlock4(val state: Feat286UiModel, val checksum: Int)
data class Feat286StateBlock5(val state: Feat286UiModel, val checksum: Int)
data class Feat286StateBlock6(val state: Feat286UiModel, val checksum: Int)
data class Feat286StateBlock7(val state: Feat286UiModel, val checksum: Int)
data class Feat286StateBlock8(val state: Feat286UiModel, val checksum: Int)
data class Feat286StateBlock9(val state: Feat286UiModel, val checksum: Int)
data class Feat286StateBlock10(val state: Feat286UiModel, val checksum: Int)

fun buildFeat286UserItem(user: CoreUser, index: Int): Feat286UserItem1 {
    return Feat286UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat286StateBlock(model: Feat286UiModel): Feat286StateBlock1 {
    return Feat286StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat286UserSummary> {
    val list = java.util.ArrayList<Feat286UserSummary>(users.size)
    for (user in users) {
        list += Feat286UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat286UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat286UiModel {
    val summaries = (0 until count).map {
        Feat286UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat286UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat286UiModel> {
    val models = java.util.ArrayList<Feat286UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat286AnalyticsEvent1(val name: String, val value: String)
data class Feat286AnalyticsEvent2(val name: String, val value: String)
data class Feat286AnalyticsEvent3(val name: String, val value: String)
data class Feat286AnalyticsEvent4(val name: String, val value: String)
data class Feat286AnalyticsEvent5(val name: String, val value: String)
data class Feat286AnalyticsEvent6(val name: String, val value: String)
data class Feat286AnalyticsEvent7(val name: String, val value: String)
data class Feat286AnalyticsEvent8(val name: String, val value: String)
data class Feat286AnalyticsEvent9(val name: String, val value: String)
data class Feat286AnalyticsEvent10(val name: String, val value: String)

fun logFeat286Event1(event: Feat286AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat286Event2(event: Feat286AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat286Event3(event: Feat286AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat286Event4(event: Feat286AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat286Event5(event: Feat286AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat286Event6(event: Feat286AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat286Event7(event: Feat286AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat286Event8(event: Feat286AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat286Event9(event: Feat286AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat286Event10(event: Feat286AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat286Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat286Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat286Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat286Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat286Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat286Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat286Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat286Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat286Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat286Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat286(u: CoreUser): Feat286Projection1 =
    Feat286Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat286Projection1> {
    val list = java.util.ArrayList<Feat286Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat286(u)
    }
    return list
}
