package com.romix.feature.feat411

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat411Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat411UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat411FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat411UserSummary
)

data class Feat411UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat411NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat411Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat411Config = Feat411Config()
) {

    fun loadSnapshot(userId: Long): Feat411NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat411NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat411UserSummary {
        return Feat411UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat411FeedItem> {
        val result = java.util.ArrayList<Feat411FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat411FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat411UiMapper {

    fun mapToUi(model: List<Feat411FeedItem>): Feat411UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat411UiModel(
            header = UiText("Feat411 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat411UiModel =
        Feat411UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat411UiModel =
        Feat411UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat411UiModel =
        Feat411UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat411Service(
    private val repository: Feat411Repository,
    private val uiMapper: Feat411UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat411UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat411UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat411UserItem1(val user: CoreUser, val label: String)
data class Feat411UserItem2(val user: CoreUser, val label: String)
data class Feat411UserItem3(val user: CoreUser, val label: String)
data class Feat411UserItem4(val user: CoreUser, val label: String)
data class Feat411UserItem5(val user: CoreUser, val label: String)
data class Feat411UserItem6(val user: CoreUser, val label: String)
data class Feat411UserItem7(val user: CoreUser, val label: String)
data class Feat411UserItem8(val user: CoreUser, val label: String)
data class Feat411UserItem9(val user: CoreUser, val label: String)
data class Feat411UserItem10(val user: CoreUser, val label: String)

data class Feat411StateBlock1(val state: Feat411UiModel, val checksum: Int)
data class Feat411StateBlock2(val state: Feat411UiModel, val checksum: Int)
data class Feat411StateBlock3(val state: Feat411UiModel, val checksum: Int)
data class Feat411StateBlock4(val state: Feat411UiModel, val checksum: Int)
data class Feat411StateBlock5(val state: Feat411UiModel, val checksum: Int)
data class Feat411StateBlock6(val state: Feat411UiModel, val checksum: Int)
data class Feat411StateBlock7(val state: Feat411UiModel, val checksum: Int)
data class Feat411StateBlock8(val state: Feat411UiModel, val checksum: Int)
data class Feat411StateBlock9(val state: Feat411UiModel, val checksum: Int)
data class Feat411StateBlock10(val state: Feat411UiModel, val checksum: Int)

fun buildFeat411UserItem(user: CoreUser, index: Int): Feat411UserItem1 {
    return Feat411UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat411StateBlock(model: Feat411UiModel): Feat411StateBlock1 {
    return Feat411StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat411UserSummary> {
    val list = java.util.ArrayList<Feat411UserSummary>(users.size)
    for (user in users) {
        list += Feat411UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat411UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat411UiModel {
    val summaries = (0 until count).map {
        Feat411UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat411UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat411UiModel> {
    val models = java.util.ArrayList<Feat411UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat411AnalyticsEvent1(val name: String, val value: String)
data class Feat411AnalyticsEvent2(val name: String, val value: String)
data class Feat411AnalyticsEvent3(val name: String, val value: String)
data class Feat411AnalyticsEvent4(val name: String, val value: String)
data class Feat411AnalyticsEvent5(val name: String, val value: String)
data class Feat411AnalyticsEvent6(val name: String, val value: String)
data class Feat411AnalyticsEvent7(val name: String, val value: String)
data class Feat411AnalyticsEvent8(val name: String, val value: String)
data class Feat411AnalyticsEvent9(val name: String, val value: String)
data class Feat411AnalyticsEvent10(val name: String, val value: String)

fun logFeat411Event1(event: Feat411AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat411Event2(event: Feat411AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat411Event3(event: Feat411AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat411Event4(event: Feat411AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat411Event5(event: Feat411AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat411Event6(event: Feat411AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat411Event7(event: Feat411AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat411Event8(event: Feat411AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat411Event9(event: Feat411AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat411Event10(event: Feat411AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat411Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat411Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat411Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat411Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat411Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat411Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat411Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat411Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat411Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat411Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat411(u: CoreUser): Feat411Projection1 =
    Feat411Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat411Projection1> {
    val list = java.util.ArrayList<Feat411Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat411(u)
    }
    return list
}
