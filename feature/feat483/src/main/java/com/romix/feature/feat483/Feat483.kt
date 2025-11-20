package com.romix.feature.feat483

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat483Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat483UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat483FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat483UserSummary
)

data class Feat483UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat483NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat483Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat483Config = Feat483Config()
) {

    fun loadSnapshot(userId: Long): Feat483NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat483NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat483UserSummary {
        return Feat483UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat483FeedItem> {
        val result = java.util.ArrayList<Feat483FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat483FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat483UiMapper {

    fun mapToUi(model: List<Feat483FeedItem>): Feat483UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat483UiModel(
            header = UiText("Feat483 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat483UiModel =
        Feat483UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat483UiModel =
        Feat483UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat483UiModel =
        Feat483UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat483Service(
    private val repository: Feat483Repository,
    private val uiMapper: Feat483UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat483UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat483UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat483UserItem1(val user: CoreUser, val label: String)
data class Feat483UserItem2(val user: CoreUser, val label: String)
data class Feat483UserItem3(val user: CoreUser, val label: String)
data class Feat483UserItem4(val user: CoreUser, val label: String)
data class Feat483UserItem5(val user: CoreUser, val label: String)
data class Feat483UserItem6(val user: CoreUser, val label: String)
data class Feat483UserItem7(val user: CoreUser, val label: String)
data class Feat483UserItem8(val user: CoreUser, val label: String)
data class Feat483UserItem9(val user: CoreUser, val label: String)
data class Feat483UserItem10(val user: CoreUser, val label: String)

data class Feat483StateBlock1(val state: Feat483UiModel, val checksum: Int)
data class Feat483StateBlock2(val state: Feat483UiModel, val checksum: Int)
data class Feat483StateBlock3(val state: Feat483UiModel, val checksum: Int)
data class Feat483StateBlock4(val state: Feat483UiModel, val checksum: Int)
data class Feat483StateBlock5(val state: Feat483UiModel, val checksum: Int)
data class Feat483StateBlock6(val state: Feat483UiModel, val checksum: Int)
data class Feat483StateBlock7(val state: Feat483UiModel, val checksum: Int)
data class Feat483StateBlock8(val state: Feat483UiModel, val checksum: Int)
data class Feat483StateBlock9(val state: Feat483UiModel, val checksum: Int)
data class Feat483StateBlock10(val state: Feat483UiModel, val checksum: Int)

fun buildFeat483UserItem(user: CoreUser, index: Int): Feat483UserItem1 {
    return Feat483UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat483StateBlock(model: Feat483UiModel): Feat483StateBlock1 {
    return Feat483StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat483UserSummary> {
    val list = java.util.ArrayList<Feat483UserSummary>(users.size)
    for (user in users) {
        list += Feat483UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat483UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat483UiModel {
    val summaries = (0 until count).map {
        Feat483UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat483UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat483UiModel> {
    val models = java.util.ArrayList<Feat483UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat483AnalyticsEvent1(val name: String, val value: String)
data class Feat483AnalyticsEvent2(val name: String, val value: String)
data class Feat483AnalyticsEvent3(val name: String, val value: String)
data class Feat483AnalyticsEvent4(val name: String, val value: String)
data class Feat483AnalyticsEvent5(val name: String, val value: String)
data class Feat483AnalyticsEvent6(val name: String, val value: String)
data class Feat483AnalyticsEvent7(val name: String, val value: String)
data class Feat483AnalyticsEvent8(val name: String, val value: String)
data class Feat483AnalyticsEvent9(val name: String, val value: String)
data class Feat483AnalyticsEvent10(val name: String, val value: String)

fun logFeat483Event1(event: Feat483AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat483Event2(event: Feat483AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat483Event3(event: Feat483AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat483Event4(event: Feat483AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat483Event5(event: Feat483AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat483Event6(event: Feat483AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat483Event7(event: Feat483AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat483Event8(event: Feat483AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat483Event9(event: Feat483AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat483Event10(event: Feat483AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat483Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat483Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat483Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat483Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat483Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat483Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat483Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat483Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat483Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat483Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat483(u: CoreUser): Feat483Projection1 =
    Feat483Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat483Projection1> {
    val list = java.util.ArrayList<Feat483Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat483(u)
    }
    return list
}
