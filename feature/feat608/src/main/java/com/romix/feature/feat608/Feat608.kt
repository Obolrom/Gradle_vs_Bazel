package com.romix.feature.feat608

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat608Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat608UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat608FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat608UserSummary
)

data class Feat608UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat608NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat608Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat608Config = Feat608Config()
) {

    fun loadSnapshot(userId: Long): Feat608NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat608NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat608UserSummary {
        return Feat608UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat608FeedItem> {
        val result = java.util.ArrayList<Feat608FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat608FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat608UiMapper {

    fun mapToUi(model: List<Feat608FeedItem>): Feat608UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat608UiModel(
            header = UiText("Feat608 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat608UiModel =
        Feat608UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat608UiModel =
        Feat608UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat608UiModel =
        Feat608UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat608Service(
    private val repository: Feat608Repository,
    private val uiMapper: Feat608UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat608UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat608UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat608UserItem1(val user: CoreUser, val label: String)
data class Feat608UserItem2(val user: CoreUser, val label: String)
data class Feat608UserItem3(val user: CoreUser, val label: String)
data class Feat608UserItem4(val user: CoreUser, val label: String)
data class Feat608UserItem5(val user: CoreUser, val label: String)
data class Feat608UserItem6(val user: CoreUser, val label: String)
data class Feat608UserItem7(val user: CoreUser, val label: String)
data class Feat608UserItem8(val user: CoreUser, val label: String)
data class Feat608UserItem9(val user: CoreUser, val label: String)
data class Feat608UserItem10(val user: CoreUser, val label: String)

data class Feat608StateBlock1(val state: Feat608UiModel, val checksum: Int)
data class Feat608StateBlock2(val state: Feat608UiModel, val checksum: Int)
data class Feat608StateBlock3(val state: Feat608UiModel, val checksum: Int)
data class Feat608StateBlock4(val state: Feat608UiModel, val checksum: Int)
data class Feat608StateBlock5(val state: Feat608UiModel, val checksum: Int)
data class Feat608StateBlock6(val state: Feat608UiModel, val checksum: Int)
data class Feat608StateBlock7(val state: Feat608UiModel, val checksum: Int)
data class Feat608StateBlock8(val state: Feat608UiModel, val checksum: Int)
data class Feat608StateBlock9(val state: Feat608UiModel, val checksum: Int)
data class Feat608StateBlock10(val state: Feat608UiModel, val checksum: Int)

fun buildFeat608UserItem(user: CoreUser, index: Int): Feat608UserItem1 {
    return Feat608UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat608StateBlock(model: Feat608UiModel): Feat608StateBlock1 {
    return Feat608StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat608UserSummary> {
    val list = java.util.ArrayList<Feat608UserSummary>(users.size)
    for (user in users) {
        list += Feat608UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat608UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat608UiModel {
    val summaries = (0 until count).map {
        Feat608UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat608UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat608UiModel> {
    val models = java.util.ArrayList<Feat608UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat608AnalyticsEvent1(val name: String, val value: String)
data class Feat608AnalyticsEvent2(val name: String, val value: String)
data class Feat608AnalyticsEvent3(val name: String, val value: String)
data class Feat608AnalyticsEvent4(val name: String, val value: String)
data class Feat608AnalyticsEvent5(val name: String, val value: String)
data class Feat608AnalyticsEvent6(val name: String, val value: String)
data class Feat608AnalyticsEvent7(val name: String, val value: String)
data class Feat608AnalyticsEvent8(val name: String, val value: String)
data class Feat608AnalyticsEvent9(val name: String, val value: String)
data class Feat608AnalyticsEvent10(val name: String, val value: String)

fun logFeat608Event1(event: Feat608AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat608Event2(event: Feat608AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat608Event3(event: Feat608AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat608Event4(event: Feat608AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat608Event5(event: Feat608AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat608Event6(event: Feat608AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat608Event7(event: Feat608AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat608Event8(event: Feat608AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat608Event9(event: Feat608AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat608Event10(event: Feat608AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat608Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat608Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat608Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat608Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat608Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat608Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat608Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat608Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat608Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat608Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat608(u: CoreUser): Feat608Projection1 =
    Feat608Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat608Projection1> {
    val list = java.util.ArrayList<Feat608Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat608(u)
    }
    return list
}
