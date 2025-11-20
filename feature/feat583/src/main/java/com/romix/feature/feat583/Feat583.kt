package com.romix.feature.feat583

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat583Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat583UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat583FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat583UserSummary
)

data class Feat583UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat583NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat583Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat583Config = Feat583Config()
) {

    fun loadSnapshot(userId: Long): Feat583NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat583NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat583UserSummary {
        return Feat583UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat583FeedItem> {
        val result = java.util.ArrayList<Feat583FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat583FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat583UiMapper {

    fun mapToUi(model: List<Feat583FeedItem>): Feat583UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat583UiModel(
            header = UiText("Feat583 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat583UiModel =
        Feat583UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat583UiModel =
        Feat583UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat583UiModel =
        Feat583UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat583Service(
    private val repository: Feat583Repository,
    private val uiMapper: Feat583UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat583UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat583UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat583UserItem1(val user: CoreUser, val label: String)
data class Feat583UserItem2(val user: CoreUser, val label: String)
data class Feat583UserItem3(val user: CoreUser, val label: String)
data class Feat583UserItem4(val user: CoreUser, val label: String)
data class Feat583UserItem5(val user: CoreUser, val label: String)
data class Feat583UserItem6(val user: CoreUser, val label: String)
data class Feat583UserItem7(val user: CoreUser, val label: String)
data class Feat583UserItem8(val user: CoreUser, val label: String)
data class Feat583UserItem9(val user: CoreUser, val label: String)
data class Feat583UserItem10(val user: CoreUser, val label: String)

data class Feat583StateBlock1(val state: Feat583UiModel, val checksum: Int)
data class Feat583StateBlock2(val state: Feat583UiModel, val checksum: Int)
data class Feat583StateBlock3(val state: Feat583UiModel, val checksum: Int)
data class Feat583StateBlock4(val state: Feat583UiModel, val checksum: Int)
data class Feat583StateBlock5(val state: Feat583UiModel, val checksum: Int)
data class Feat583StateBlock6(val state: Feat583UiModel, val checksum: Int)
data class Feat583StateBlock7(val state: Feat583UiModel, val checksum: Int)
data class Feat583StateBlock8(val state: Feat583UiModel, val checksum: Int)
data class Feat583StateBlock9(val state: Feat583UiModel, val checksum: Int)
data class Feat583StateBlock10(val state: Feat583UiModel, val checksum: Int)

fun buildFeat583UserItem(user: CoreUser, index: Int): Feat583UserItem1 {
    return Feat583UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat583StateBlock(model: Feat583UiModel): Feat583StateBlock1 {
    return Feat583StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat583UserSummary> {
    val list = java.util.ArrayList<Feat583UserSummary>(users.size)
    for (user in users) {
        list += Feat583UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat583UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat583UiModel {
    val summaries = (0 until count).map {
        Feat583UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat583UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat583UiModel> {
    val models = java.util.ArrayList<Feat583UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat583AnalyticsEvent1(val name: String, val value: String)
data class Feat583AnalyticsEvent2(val name: String, val value: String)
data class Feat583AnalyticsEvent3(val name: String, val value: String)
data class Feat583AnalyticsEvent4(val name: String, val value: String)
data class Feat583AnalyticsEvent5(val name: String, val value: String)
data class Feat583AnalyticsEvent6(val name: String, val value: String)
data class Feat583AnalyticsEvent7(val name: String, val value: String)
data class Feat583AnalyticsEvent8(val name: String, val value: String)
data class Feat583AnalyticsEvent9(val name: String, val value: String)
data class Feat583AnalyticsEvent10(val name: String, val value: String)

fun logFeat583Event1(event: Feat583AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat583Event2(event: Feat583AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat583Event3(event: Feat583AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat583Event4(event: Feat583AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat583Event5(event: Feat583AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat583Event6(event: Feat583AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat583Event7(event: Feat583AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat583Event8(event: Feat583AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat583Event9(event: Feat583AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat583Event10(event: Feat583AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat583Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat583Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat583Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat583Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat583Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat583Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat583Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat583Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat583Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat583Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat583(u: CoreUser): Feat583Projection1 =
    Feat583Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat583Projection1> {
    val list = java.util.ArrayList<Feat583Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat583(u)
    }
    return list
}
