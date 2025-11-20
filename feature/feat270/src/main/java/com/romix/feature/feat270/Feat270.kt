package com.romix.feature.feat270

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat270Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat270UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat270FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat270UserSummary
)

data class Feat270UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat270NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat270Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat270Config = Feat270Config()
) {

    fun loadSnapshot(userId: Long): Feat270NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat270NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat270UserSummary {
        return Feat270UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat270FeedItem> {
        val result = java.util.ArrayList<Feat270FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat270FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat270UiMapper {

    fun mapToUi(model: List<Feat270FeedItem>): Feat270UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat270UiModel(
            header = UiText("Feat270 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat270UiModel =
        Feat270UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat270UiModel =
        Feat270UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat270UiModel =
        Feat270UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat270Service(
    private val repository: Feat270Repository,
    private val uiMapper: Feat270UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat270UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat270UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat270UserItem1(val user: CoreUser, val label: String)
data class Feat270UserItem2(val user: CoreUser, val label: String)
data class Feat270UserItem3(val user: CoreUser, val label: String)
data class Feat270UserItem4(val user: CoreUser, val label: String)
data class Feat270UserItem5(val user: CoreUser, val label: String)
data class Feat270UserItem6(val user: CoreUser, val label: String)
data class Feat270UserItem7(val user: CoreUser, val label: String)
data class Feat270UserItem8(val user: CoreUser, val label: String)
data class Feat270UserItem9(val user: CoreUser, val label: String)
data class Feat270UserItem10(val user: CoreUser, val label: String)

data class Feat270StateBlock1(val state: Feat270UiModel, val checksum: Int)
data class Feat270StateBlock2(val state: Feat270UiModel, val checksum: Int)
data class Feat270StateBlock3(val state: Feat270UiModel, val checksum: Int)
data class Feat270StateBlock4(val state: Feat270UiModel, val checksum: Int)
data class Feat270StateBlock5(val state: Feat270UiModel, val checksum: Int)
data class Feat270StateBlock6(val state: Feat270UiModel, val checksum: Int)
data class Feat270StateBlock7(val state: Feat270UiModel, val checksum: Int)
data class Feat270StateBlock8(val state: Feat270UiModel, val checksum: Int)
data class Feat270StateBlock9(val state: Feat270UiModel, val checksum: Int)
data class Feat270StateBlock10(val state: Feat270UiModel, val checksum: Int)

fun buildFeat270UserItem(user: CoreUser, index: Int): Feat270UserItem1 {
    return Feat270UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat270StateBlock(model: Feat270UiModel): Feat270StateBlock1 {
    return Feat270StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat270UserSummary> {
    val list = java.util.ArrayList<Feat270UserSummary>(users.size)
    for (user in users) {
        list += Feat270UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat270UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat270UiModel {
    val summaries = (0 until count).map {
        Feat270UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat270UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat270UiModel> {
    val models = java.util.ArrayList<Feat270UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat270AnalyticsEvent1(val name: String, val value: String)
data class Feat270AnalyticsEvent2(val name: String, val value: String)
data class Feat270AnalyticsEvent3(val name: String, val value: String)
data class Feat270AnalyticsEvent4(val name: String, val value: String)
data class Feat270AnalyticsEvent5(val name: String, val value: String)
data class Feat270AnalyticsEvent6(val name: String, val value: String)
data class Feat270AnalyticsEvent7(val name: String, val value: String)
data class Feat270AnalyticsEvent8(val name: String, val value: String)
data class Feat270AnalyticsEvent9(val name: String, val value: String)
data class Feat270AnalyticsEvent10(val name: String, val value: String)

fun logFeat270Event1(event: Feat270AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat270Event2(event: Feat270AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat270Event3(event: Feat270AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat270Event4(event: Feat270AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat270Event5(event: Feat270AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat270Event6(event: Feat270AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat270Event7(event: Feat270AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat270Event8(event: Feat270AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat270Event9(event: Feat270AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat270Event10(event: Feat270AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat270Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat270Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat270Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat270Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat270Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat270Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat270Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat270Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat270Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat270Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat270(u: CoreUser): Feat270Projection1 =
    Feat270Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat270Projection1> {
    val list = java.util.ArrayList<Feat270Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat270(u)
    }
    return list
}
