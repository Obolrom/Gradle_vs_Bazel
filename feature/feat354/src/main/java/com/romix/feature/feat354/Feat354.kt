package com.romix.feature.feat354

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat354Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat354UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat354FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat354UserSummary
)

data class Feat354UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat354NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat354Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat354Config = Feat354Config()
) {

    fun loadSnapshot(userId: Long): Feat354NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat354NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat354UserSummary {
        return Feat354UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat354FeedItem> {
        val result = java.util.ArrayList<Feat354FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat354FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat354UiMapper {

    fun mapToUi(model: List<Feat354FeedItem>): Feat354UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat354UiModel(
            header = UiText("Feat354 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat354UiModel =
        Feat354UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat354UiModel =
        Feat354UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat354UiModel =
        Feat354UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat354Service(
    private val repository: Feat354Repository,
    private val uiMapper: Feat354UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat354UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat354UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat354UserItem1(val user: CoreUser, val label: String)
data class Feat354UserItem2(val user: CoreUser, val label: String)
data class Feat354UserItem3(val user: CoreUser, val label: String)
data class Feat354UserItem4(val user: CoreUser, val label: String)
data class Feat354UserItem5(val user: CoreUser, val label: String)
data class Feat354UserItem6(val user: CoreUser, val label: String)
data class Feat354UserItem7(val user: CoreUser, val label: String)
data class Feat354UserItem8(val user: CoreUser, val label: String)
data class Feat354UserItem9(val user: CoreUser, val label: String)
data class Feat354UserItem10(val user: CoreUser, val label: String)

data class Feat354StateBlock1(val state: Feat354UiModel, val checksum: Int)
data class Feat354StateBlock2(val state: Feat354UiModel, val checksum: Int)
data class Feat354StateBlock3(val state: Feat354UiModel, val checksum: Int)
data class Feat354StateBlock4(val state: Feat354UiModel, val checksum: Int)
data class Feat354StateBlock5(val state: Feat354UiModel, val checksum: Int)
data class Feat354StateBlock6(val state: Feat354UiModel, val checksum: Int)
data class Feat354StateBlock7(val state: Feat354UiModel, val checksum: Int)
data class Feat354StateBlock8(val state: Feat354UiModel, val checksum: Int)
data class Feat354StateBlock9(val state: Feat354UiModel, val checksum: Int)
data class Feat354StateBlock10(val state: Feat354UiModel, val checksum: Int)

fun buildFeat354UserItem(user: CoreUser, index: Int): Feat354UserItem1 {
    return Feat354UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat354StateBlock(model: Feat354UiModel): Feat354StateBlock1 {
    return Feat354StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat354UserSummary> {
    val list = java.util.ArrayList<Feat354UserSummary>(users.size)
    for (user in users) {
        list += Feat354UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat354UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat354UiModel {
    val summaries = (0 until count).map {
        Feat354UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat354UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat354UiModel> {
    val models = java.util.ArrayList<Feat354UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat354AnalyticsEvent1(val name: String, val value: String)
data class Feat354AnalyticsEvent2(val name: String, val value: String)
data class Feat354AnalyticsEvent3(val name: String, val value: String)
data class Feat354AnalyticsEvent4(val name: String, val value: String)
data class Feat354AnalyticsEvent5(val name: String, val value: String)
data class Feat354AnalyticsEvent6(val name: String, val value: String)
data class Feat354AnalyticsEvent7(val name: String, val value: String)
data class Feat354AnalyticsEvent8(val name: String, val value: String)
data class Feat354AnalyticsEvent9(val name: String, val value: String)
data class Feat354AnalyticsEvent10(val name: String, val value: String)

fun logFeat354Event1(event: Feat354AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat354Event2(event: Feat354AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat354Event3(event: Feat354AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat354Event4(event: Feat354AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat354Event5(event: Feat354AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat354Event6(event: Feat354AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat354Event7(event: Feat354AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat354Event8(event: Feat354AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat354Event9(event: Feat354AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat354Event10(event: Feat354AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat354Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat354Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat354Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat354Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat354Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat354Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat354Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat354Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat354Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat354Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat354(u: CoreUser): Feat354Projection1 =
    Feat354Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat354Projection1> {
    val list = java.util.ArrayList<Feat354Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat354(u)
    }
    return list
}
