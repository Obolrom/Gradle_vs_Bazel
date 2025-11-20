package com.romix.feature.feat558

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat558Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat558UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat558FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat558UserSummary
)

data class Feat558UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat558NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat558Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat558Config = Feat558Config()
) {

    fun loadSnapshot(userId: Long): Feat558NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat558NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat558UserSummary {
        return Feat558UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat558FeedItem> {
        val result = java.util.ArrayList<Feat558FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat558FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat558UiMapper {

    fun mapToUi(model: List<Feat558FeedItem>): Feat558UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat558UiModel(
            header = UiText("Feat558 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat558UiModel =
        Feat558UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat558UiModel =
        Feat558UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat558UiModel =
        Feat558UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat558Service(
    private val repository: Feat558Repository,
    private val uiMapper: Feat558UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat558UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat558UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat558UserItem1(val user: CoreUser, val label: String)
data class Feat558UserItem2(val user: CoreUser, val label: String)
data class Feat558UserItem3(val user: CoreUser, val label: String)
data class Feat558UserItem4(val user: CoreUser, val label: String)
data class Feat558UserItem5(val user: CoreUser, val label: String)
data class Feat558UserItem6(val user: CoreUser, val label: String)
data class Feat558UserItem7(val user: CoreUser, val label: String)
data class Feat558UserItem8(val user: CoreUser, val label: String)
data class Feat558UserItem9(val user: CoreUser, val label: String)
data class Feat558UserItem10(val user: CoreUser, val label: String)

data class Feat558StateBlock1(val state: Feat558UiModel, val checksum: Int)
data class Feat558StateBlock2(val state: Feat558UiModel, val checksum: Int)
data class Feat558StateBlock3(val state: Feat558UiModel, val checksum: Int)
data class Feat558StateBlock4(val state: Feat558UiModel, val checksum: Int)
data class Feat558StateBlock5(val state: Feat558UiModel, val checksum: Int)
data class Feat558StateBlock6(val state: Feat558UiModel, val checksum: Int)
data class Feat558StateBlock7(val state: Feat558UiModel, val checksum: Int)
data class Feat558StateBlock8(val state: Feat558UiModel, val checksum: Int)
data class Feat558StateBlock9(val state: Feat558UiModel, val checksum: Int)
data class Feat558StateBlock10(val state: Feat558UiModel, val checksum: Int)

fun buildFeat558UserItem(user: CoreUser, index: Int): Feat558UserItem1 {
    return Feat558UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat558StateBlock(model: Feat558UiModel): Feat558StateBlock1 {
    return Feat558StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat558UserSummary> {
    val list = java.util.ArrayList<Feat558UserSummary>(users.size)
    for (user in users) {
        list += Feat558UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat558UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat558UiModel {
    val summaries = (0 until count).map {
        Feat558UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat558UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat558UiModel> {
    val models = java.util.ArrayList<Feat558UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat558AnalyticsEvent1(val name: String, val value: String)
data class Feat558AnalyticsEvent2(val name: String, val value: String)
data class Feat558AnalyticsEvent3(val name: String, val value: String)
data class Feat558AnalyticsEvent4(val name: String, val value: String)
data class Feat558AnalyticsEvent5(val name: String, val value: String)
data class Feat558AnalyticsEvent6(val name: String, val value: String)
data class Feat558AnalyticsEvent7(val name: String, val value: String)
data class Feat558AnalyticsEvent8(val name: String, val value: String)
data class Feat558AnalyticsEvent9(val name: String, val value: String)
data class Feat558AnalyticsEvent10(val name: String, val value: String)

fun logFeat558Event1(event: Feat558AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat558Event2(event: Feat558AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat558Event3(event: Feat558AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat558Event4(event: Feat558AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat558Event5(event: Feat558AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat558Event6(event: Feat558AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat558Event7(event: Feat558AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat558Event8(event: Feat558AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat558Event9(event: Feat558AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat558Event10(event: Feat558AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat558Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat558Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat558Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat558Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat558Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat558Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat558Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat558Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat558Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat558Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat558(u: CoreUser): Feat558Projection1 =
    Feat558Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat558Projection1> {
    val list = java.util.ArrayList<Feat558Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat558(u)
    }
    return list
}
