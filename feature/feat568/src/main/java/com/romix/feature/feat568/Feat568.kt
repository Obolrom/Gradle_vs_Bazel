package com.romix.feature.feat568

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat568Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat568UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat568FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat568UserSummary
)

data class Feat568UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat568NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat568Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat568Config = Feat568Config()
) {

    fun loadSnapshot(userId: Long): Feat568NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat568NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat568UserSummary {
        return Feat568UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat568FeedItem> {
        val result = java.util.ArrayList<Feat568FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat568FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat568UiMapper {

    fun mapToUi(model: List<Feat568FeedItem>): Feat568UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat568UiModel(
            header = UiText("Feat568 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat568UiModel =
        Feat568UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat568UiModel =
        Feat568UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat568UiModel =
        Feat568UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat568Service(
    private val repository: Feat568Repository,
    private val uiMapper: Feat568UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat568UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat568UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat568UserItem1(val user: CoreUser, val label: String)
data class Feat568UserItem2(val user: CoreUser, val label: String)
data class Feat568UserItem3(val user: CoreUser, val label: String)
data class Feat568UserItem4(val user: CoreUser, val label: String)
data class Feat568UserItem5(val user: CoreUser, val label: String)
data class Feat568UserItem6(val user: CoreUser, val label: String)
data class Feat568UserItem7(val user: CoreUser, val label: String)
data class Feat568UserItem8(val user: CoreUser, val label: String)
data class Feat568UserItem9(val user: CoreUser, val label: String)
data class Feat568UserItem10(val user: CoreUser, val label: String)

data class Feat568StateBlock1(val state: Feat568UiModel, val checksum: Int)
data class Feat568StateBlock2(val state: Feat568UiModel, val checksum: Int)
data class Feat568StateBlock3(val state: Feat568UiModel, val checksum: Int)
data class Feat568StateBlock4(val state: Feat568UiModel, val checksum: Int)
data class Feat568StateBlock5(val state: Feat568UiModel, val checksum: Int)
data class Feat568StateBlock6(val state: Feat568UiModel, val checksum: Int)
data class Feat568StateBlock7(val state: Feat568UiModel, val checksum: Int)
data class Feat568StateBlock8(val state: Feat568UiModel, val checksum: Int)
data class Feat568StateBlock9(val state: Feat568UiModel, val checksum: Int)
data class Feat568StateBlock10(val state: Feat568UiModel, val checksum: Int)

fun buildFeat568UserItem(user: CoreUser, index: Int): Feat568UserItem1 {
    return Feat568UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat568StateBlock(model: Feat568UiModel): Feat568StateBlock1 {
    return Feat568StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat568UserSummary> {
    val list = java.util.ArrayList<Feat568UserSummary>(users.size)
    for (user in users) {
        list += Feat568UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat568UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat568UiModel {
    val summaries = (0 until count).map {
        Feat568UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat568UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat568UiModel> {
    val models = java.util.ArrayList<Feat568UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat568AnalyticsEvent1(val name: String, val value: String)
data class Feat568AnalyticsEvent2(val name: String, val value: String)
data class Feat568AnalyticsEvent3(val name: String, val value: String)
data class Feat568AnalyticsEvent4(val name: String, val value: String)
data class Feat568AnalyticsEvent5(val name: String, val value: String)
data class Feat568AnalyticsEvent6(val name: String, val value: String)
data class Feat568AnalyticsEvent7(val name: String, val value: String)
data class Feat568AnalyticsEvent8(val name: String, val value: String)
data class Feat568AnalyticsEvent9(val name: String, val value: String)
data class Feat568AnalyticsEvent10(val name: String, val value: String)

fun logFeat568Event1(event: Feat568AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat568Event2(event: Feat568AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat568Event3(event: Feat568AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat568Event4(event: Feat568AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat568Event5(event: Feat568AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat568Event6(event: Feat568AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat568Event7(event: Feat568AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat568Event8(event: Feat568AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat568Event9(event: Feat568AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat568Event10(event: Feat568AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat568Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat568Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat568Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat568Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat568Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat568Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat568Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat568Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat568Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat568Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat568(u: CoreUser): Feat568Projection1 =
    Feat568Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat568Projection1> {
    val list = java.util.ArrayList<Feat568Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat568(u)
    }
    return list
}
