package com.romix.feature.feat359

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat359Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat359UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat359FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat359UserSummary
)

data class Feat359UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat359NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat359Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat359Config = Feat359Config()
) {

    fun loadSnapshot(userId: Long): Feat359NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat359NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat359UserSummary {
        return Feat359UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat359FeedItem> {
        val result = java.util.ArrayList<Feat359FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat359FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat359UiMapper {

    fun mapToUi(model: List<Feat359FeedItem>): Feat359UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat359UiModel(
            header = UiText("Feat359 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat359UiModel =
        Feat359UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat359UiModel =
        Feat359UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat359UiModel =
        Feat359UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat359Service(
    private val repository: Feat359Repository,
    private val uiMapper: Feat359UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat359UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat359UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat359UserItem1(val user: CoreUser, val label: String)
data class Feat359UserItem2(val user: CoreUser, val label: String)
data class Feat359UserItem3(val user: CoreUser, val label: String)
data class Feat359UserItem4(val user: CoreUser, val label: String)
data class Feat359UserItem5(val user: CoreUser, val label: String)
data class Feat359UserItem6(val user: CoreUser, val label: String)
data class Feat359UserItem7(val user: CoreUser, val label: String)
data class Feat359UserItem8(val user: CoreUser, val label: String)
data class Feat359UserItem9(val user: CoreUser, val label: String)
data class Feat359UserItem10(val user: CoreUser, val label: String)

data class Feat359StateBlock1(val state: Feat359UiModel, val checksum: Int)
data class Feat359StateBlock2(val state: Feat359UiModel, val checksum: Int)
data class Feat359StateBlock3(val state: Feat359UiModel, val checksum: Int)
data class Feat359StateBlock4(val state: Feat359UiModel, val checksum: Int)
data class Feat359StateBlock5(val state: Feat359UiModel, val checksum: Int)
data class Feat359StateBlock6(val state: Feat359UiModel, val checksum: Int)
data class Feat359StateBlock7(val state: Feat359UiModel, val checksum: Int)
data class Feat359StateBlock8(val state: Feat359UiModel, val checksum: Int)
data class Feat359StateBlock9(val state: Feat359UiModel, val checksum: Int)
data class Feat359StateBlock10(val state: Feat359UiModel, val checksum: Int)

fun buildFeat359UserItem(user: CoreUser, index: Int): Feat359UserItem1 {
    return Feat359UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat359StateBlock(model: Feat359UiModel): Feat359StateBlock1 {
    return Feat359StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat359UserSummary> {
    val list = java.util.ArrayList<Feat359UserSummary>(users.size)
    for (user in users) {
        list += Feat359UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat359UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat359UiModel {
    val summaries = (0 until count).map {
        Feat359UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat359UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat359UiModel> {
    val models = java.util.ArrayList<Feat359UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat359AnalyticsEvent1(val name: String, val value: String)
data class Feat359AnalyticsEvent2(val name: String, val value: String)
data class Feat359AnalyticsEvent3(val name: String, val value: String)
data class Feat359AnalyticsEvent4(val name: String, val value: String)
data class Feat359AnalyticsEvent5(val name: String, val value: String)
data class Feat359AnalyticsEvent6(val name: String, val value: String)
data class Feat359AnalyticsEvent7(val name: String, val value: String)
data class Feat359AnalyticsEvent8(val name: String, val value: String)
data class Feat359AnalyticsEvent9(val name: String, val value: String)
data class Feat359AnalyticsEvent10(val name: String, val value: String)

fun logFeat359Event1(event: Feat359AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat359Event2(event: Feat359AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat359Event3(event: Feat359AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat359Event4(event: Feat359AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat359Event5(event: Feat359AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat359Event6(event: Feat359AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat359Event7(event: Feat359AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat359Event8(event: Feat359AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat359Event9(event: Feat359AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat359Event10(event: Feat359AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat359Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat359Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat359Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat359Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat359Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat359Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat359Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat359Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat359Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat359Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat359(u: CoreUser): Feat359Projection1 =
    Feat359Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat359Projection1> {
    val list = java.util.ArrayList<Feat359Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat359(u)
    }
    return list
}
