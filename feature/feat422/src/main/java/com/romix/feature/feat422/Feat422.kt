package com.romix.feature.feat422

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat422Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat422UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat422FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat422UserSummary
)

data class Feat422UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat422NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat422Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat422Config = Feat422Config()
) {

    fun loadSnapshot(userId: Long): Feat422NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat422NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat422UserSummary {
        return Feat422UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat422FeedItem> {
        val result = java.util.ArrayList<Feat422FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat422FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat422UiMapper {

    fun mapToUi(model: List<Feat422FeedItem>): Feat422UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat422UiModel(
            header = UiText("Feat422 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat422UiModel =
        Feat422UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat422UiModel =
        Feat422UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat422UiModel =
        Feat422UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat422Service(
    private val repository: Feat422Repository,
    private val uiMapper: Feat422UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat422UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat422UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat422UserItem1(val user: CoreUser, val label: String)
data class Feat422UserItem2(val user: CoreUser, val label: String)
data class Feat422UserItem3(val user: CoreUser, val label: String)
data class Feat422UserItem4(val user: CoreUser, val label: String)
data class Feat422UserItem5(val user: CoreUser, val label: String)
data class Feat422UserItem6(val user: CoreUser, val label: String)
data class Feat422UserItem7(val user: CoreUser, val label: String)
data class Feat422UserItem8(val user: CoreUser, val label: String)
data class Feat422UserItem9(val user: CoreUser, val label: String)
data class Feat422UserItem10(val user: CoreUser, val label: String)

data class Feat422StateBlock1(val state: Feat422UiModel, val checksum: Int)
data class Feat422StateBlock2(val state: Feat422UiModel, val checksum: Int)
data class Feat422StateBlock3(val state: Feat422UiModel, val checksum: Int)
data class Feat422StateBlock4(val state: Feat422UiModel, val checksum: Int)
data class Feat422StateBlock5(val state: Feat422UiModel, val checksum: Int)
data class Feat422StateBlock6(val state: Feat422UiModel, val checksum: Int)
data class Feat422StateBlock7(val state: Feat422UiModel, val checksum: Int)
data class Feat422StateBlock8(val state: Feat422UiModel, val checksum: Int)
data class Feat422StateBlock9(val state: Feat422UiModel, val checksum: Int)
data class Feat422StateBlock10(val state: Feat422UiModel, val checksum: Int)

fun buildFeat422UserItem(user: CoreUser, index: Int): Feat422UserItem1 {
    return Feat422UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat422StateBlock(model: Feat422UiModel): Feat422StateBlock1 {
    return Feat422StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat422UserSummary> {
    val list = java.util.ArrayList<Feat422UserSummary>(users.size)
    for (user in users) {
        list += Feat422UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat422UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat422UiModel {
    val summaries = (0 until count).map {
        Feat422UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat422UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat422UiModel> {
    val models = java.util.ArrayList<Feat422UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat422AnalyticsEvent1(val name: String, val value: String)
data class Feat422AnalyticsEvent2(val name: String, val value: String)
data class Feat422AnalyticsEvent3(val name: String, val value: String)
data class Feat422AnalyticsEvent4(val name: String, val value: String)
data class Feat422AnalyticsEvent5(val name: String, val value: String)
data class Feat422AnalyticsEvent6(val name: String, val value: String)
data class Feat422AnalyticsEvent7(val name: String, val value: String)
data class Feat422AnalyticsEvent8(val name: String, val value: String)
data class Feat422AnalyticsEvent9(val name: String, val value: String)
data class Feat422AnalyticsEvent10(val name: String, val value: String)

fun logFeat422Event1(event: Feat422AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat422Event2(event: Feat422AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat422Event3(event: Feat422AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat422Event4(event: Feat422AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat422Event5(event: Feat422AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat422Event6(event: Feat422AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat422Event7(event: Feat422AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat422Event8(event: Feat422AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat422Event9(event: Feat422AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat422Event10(event: Feat422AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat422Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat422Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat422Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat422Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat422Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat422Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat422Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat422Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat422Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat422Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat422(u: CoreUser): Feat422Projection1 =
    Feat422Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat422Projection1> {
    val list = java.util.ArrayList<Feat422Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat422(u)
    }
    return list
}
