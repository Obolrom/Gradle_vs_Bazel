package com.romix.feature.feat280

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat280Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat280UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat280FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat280UserSummary
)

data class Feat280UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat280NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat280Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat280Config = Feat280Config()
) {

    fun loadSnapshot(userId: Long): Feat280NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat280NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat280UserSummary {
        return Feat280UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat280FeedItem> {
        val result = java.util.ArrayList<Feat280FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat280FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat280UiMapper {

    fun mapToUi(model: List<Feat280FeedItem>): Feat280UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat280UiModel(
            header = UiText("Feat280 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat280UiModel =
        Feat280UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat280UiModel =
        Feat280UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat280UiModel =
        Feat280UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat280Service(
    private val repository: Feat280Repository,
    private val uiMapper: Feat280UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat280UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat280UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat280UserItem1(val user: CoreUser, val label: String)
data class Feat280UserItem2(val user: CoreUser, val label: String)
data class Feat280UserItem3(val user: CoreUser, val label: String)
data class Feat280UserItem4(val user: CoreUser, val label: String)
data class Feat280UserItem5(val user: CoreUser, val label: String)
data class Feat280UserItem6(val user: CoreUser, val label: String)
data class Feat280UserItem7(val user: CoreUser, val label: String)
data class Feat280UserItem8(val user: CoreUser, val label: String)
data class Feat280UserItem9(val user: CoreUser, val label: String)
data class Feat280UserItem10(val user: CoreUser, val label: String)

data class Feat280StateBlock1(val state: Feat280UiModel, val checksum: Int)
data class Feat280StateBlock2(val state: Feat280UiModel, val checksum: Int)
data class Feat280StateBlock3(val state: Feat280UiModel, val checksum: Int)
data class Feat280StateBlock4(val state: Feat280UiModel, val checksum: Int)
data class Feat280StateBlock5(val state: Feat280UiModel, val checksum: Int)
data class Feat280StateBlock6(val state: Feat280UiModel, val checksum: Int)
data class Feat280StateBlock7(val state: Feat280UiModel, val checksum: Int)
data class Feat280StateBlock8(val state: Feat280UiModel, val checksum: Int)
data class Feat280StateBlock9(val state: Feat280UiModel, val checksum: Int)
data class Feat280StateBlock10(val state: Feat280UiModel, val checksum: Int)

fun buildFeat280UserItem(user: CoreUser, index: Int): Feat280UserItem1 {
    return Feat280UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat280StateBlock(model: Feat280UiModel): Feat280StateBlock1 {
    return Feat280StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat280UserSummary> {
    val list = java.util.ArrayList<Feat280UserSummary>(users.size)
    for (user in users) {
        list += Feat280UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat280UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat280UiModel {
    val summaries = (0 until count).map {
        Feat280UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat280UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat280UiModel> {
    val models = java.util.ArrayList<Feat280UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat280AnalyticsEvent1(val name: String, val value: String)
data class Feat280AnalyticsEvent2(val name: String, val value: String)
data class Feat280AnalyticsEvent3(val name: String, val value: String)
data class Feat280AnalyticsEvent4(val name: String, val value: String)
data class Feat280AnalyticsEvent5(val name: String, val value: String)
data class Feat280AnalyticsEvent6(val name: String, val value: String)
data class Feat280AnalyticsEvent7(val name: String, val value: String)
data class Feat280AnalyticsEvent8(val name: String, val value: String)
data class Feat280AnalyticsEvent9(val name: String, val value: String)
data class Feat280AnalyticsEvent10(val name: String, val value: String)

fun logFeat280Event1(event: Feat280AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat280Event2(event: Feat280AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat280Event3(event: Feat280AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat280Event4(event: Feat280AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat280Event5(event: Feat280AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat280Event6(event: Feat280AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat280Event7(event: Feat280AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat280Event8(event: Feat280AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat280Event9(event: Feat280AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat280Event10(event: Feat280AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat280Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat280Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat280Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat280Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat280Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat280Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat280Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat280Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat280Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat280Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat280(u: CoreUser): Feat280Projection1 =
    Feat280Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat280Projection1> {
    val list = java.util.ArrayList<Feat280Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat280(u)
    }
    return list
}
