package com.romix.feature.feat249

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat249Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat249UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat249FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat249UserSummary
)

data class Feat249UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat249NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat249Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat249Config = Feat249Config()
) {

    fun loadSnapshot(userId: Long): Feat249NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat249NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat249UserSummary {
        return Feat249UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat249FeedItem> {
        val result = java.util.ArrayList<Feat249FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat249FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat249UiMapper {

    fun mapToUi(model: List<Feat249FeedItem>): Feat249UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat249UiModel(
            header = UiText("Feat249 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat249UiModel =
        Feat249UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat249UiModel =
        Feat249UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat249UiModel =
        Feat249UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat249Service(
    private val repository: Feat249Repository,
    private val uiMapper: Feat249UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat249UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat249UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat249UserItem1(val user: CoreUser, val label: String)
data class Feat249UserItem2(val user: CoreUser, val label: String)
data class Feat249UserItem3(val user: CoreUser, val label: String)
data class Feat249UserItem4(val user: CoreUser, val label: String)
data class Feat249UserItem5(val user: CoreUser, val label: String)
data class Feat249UserItem6(val user: CoreUser, val label: String)
data class Feat249UserItem7(val user: CoreUser, val label: String)
data class Feat249UserItem8(val user: CoreUser, val label: String)
data class Feat249UserItem9(val user: CoreUser, val label: String)
data class Feat249UserItem10(val user: CoreUser, val label: String)

data class Feat249StateBlock1(val state: Feat249UiModel, val checksum: Int)
data class Feat249StateBlock2(val state: Feat249UiModel, val checksum: Int)
data class Feat249StateBlock3(val state: Feat249UiModel, val checksum: Int)
data class Feat249StateBlock4(val state: Feat249UiModel, val checksum: Int)
data class Feat249StateBlock5(val state: Feat249UiModel, val checksum: Int)
data class Feat249StateBlock6(val state: Feat249UiModel, val checksum: Int)
data class Feat249StateBlock7(val state: Feat249UiModel, val checksum: Int)
data class Feat249StateBlock8(val state: Feat249UiModel, val checksum: Int)
data class Feat249StateBlock9(val state: Feat249UiModel, val checksum: Int)
data class Feat249StateBlock10(val state: Feat249UiModel, val checksum: Int)

fun buildFeat249UserItem(user: CoreUser, index: Int): Feat249UserItem1 {
    return Feat249UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat249StateBlock(model: Feat249UiModel): Feat249StateBlock1 {
    return Feat249StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat249UserSummary> {
    val list = java.util.ArrayList<Feat249UserSummary>(users.size)
    for (user in users) {
        list += Feat249UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat249UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat249UiModel {
    val summaries = (0 until count).map {
        Feat249UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat249UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat249UiModel> {
    val models = java.util.ArrayList<Feat249UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat249AnalyticsEvent1(val name: String, val value: String)
data class Feat249AnalyticsEvent2(val name: String, val value: String)
data class Feat249AnalyticsEvent3(val name: String, val value: String)
data class Feat249AnalyticsEvent4(val name: String, val value: String)
data class Feat249AnalyticsEvent5(val name: String, val value: String)
data class Feat249AnalyticsEvent6(val name: String, val value: String)
data class Feat249AnalyticsEvent7(val name: String, val value: String)
data class Feat249AnalyticsEvent8(val name: String, val value: String)
data class Feat249AnalyticsEvent9(val name: String, val value: String)
data class Feat249AnalyticsEvent10(val name: String, val value: String)

fun logFeat249Event1(event: Feat249AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat249Event2(event: Feat249AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat249Event3(event: Feat249AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat249Event4(event: Feat249AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat249Event5(event: Feat249AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat249Event6(event: Feat249AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat249Event7(event: Feat249AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat249Event8(event: Feat249AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat249Event9(event: Feat249AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat249Event10(event: Feat249AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat249Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat249Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat249Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat249Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat249Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat249Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat249Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat249Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat249Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat249Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat249(u: CoreUser): Feat249Projection1 =
    Feat249Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat249Projection1> {
    val list = java.util.ArrayList<Feat249Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat249(u)
    }
    return list
}
