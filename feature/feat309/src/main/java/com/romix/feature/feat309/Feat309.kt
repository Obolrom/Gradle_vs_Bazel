package com.romix.feature.feat309

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat309Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat309UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat309FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat309UserSummary
)

data class Feat309UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat309NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat309Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat309Config = Feat309Config()
) {

    fun loadSnapshot(userId: Long): Feat309NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat309NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat309UserSummary {
        return Feat309UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat309FeedItem> {
        val result = java.util.ArrayList<Feat309FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat309FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat309UiMapper {

    fun mapToUi(model: List<Feat309FeedItem>): Feat309UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat309UiModel(
            header = UiText("Feat309 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat309UiModel =
        Feat309UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat309UiModel =
        Feat309UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat309UiModel =
        Feat309UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat309Service(
    private val repository: Feat309Repository,
    private val uiMapper: Feat309UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat309UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat309UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat309UserItem1(val user: CoreUser, val label: String)
data class Feat309UserItem2(val user: CoreUser, val label: String)
data class Feat309UserItem3(val user: CoreUser, val label: String)
data class Feat309UserItem4(val user: CoreUser, val label: String)
data class Feat309UserItem5(val user: CoreUser, val label: String)
data class Feat309UserItem6(val user: CoreUser, val label: String)
data class Feat309UserItem7(val user: CoreUser, val label: String)
data class Feat309UserItem8(val user: CoreUser, val label: String)
data class Feat309UserItem9(val user: CoreUser, val label: String)
data class Feat309UserItem10(val user: CoreUser, val label: String)

data class Feat309StateBlock1(val state: Feat309UiModel, val checksum: Int)
data class Feat309StateBlock2(val state: Feat309UiModel, val checksum: Int)
data class Feat309StateBlock3(val state: Feat309UiModel, val checksum: Int)
data class Feat309StateBlock4(val state: Feat309UiModel, val checksum: Int)
data class Feat309StateBlock5(val state: Feat309UiModel, val checksum: Int)
data class Feat309StateBlock6(val state: Feat309UiModel, val checksum: Int)
data class Feat309StateBlock7(val state: Feat309UiModel, val checksum: Int)
data class Feat309StateBlock8(val state: Feat309UiModel, val checksum: Int)
data class Feat309StateBlock9(val state: Feat309UiModel, val checksum: Int)
data class Feat309StateBlock10(val state: Feat309UiModel, val checksum: Int)

fun buildFeat309UserItem(user: CoreUser, index: Int): Feat309UserItem1 {
    return Feat309UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat309StateBlock(model: Feat309UiModel): Feat309StateBlock1 {
    return Feat309StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat309UserSummary> {
    val list = java.util.ArrayList<Feat309UserSummary>(users.size)
    for (user in users) {
        list += Feat309UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat309UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat309UiModel {
    val summaries = (0 until count).map {
        Feat309UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat309UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat309UiModel> {
    val models = java.util.ArrayList<Feat309UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat309AnalyticsEvent1(val name: String, val value: String)
data class Feat309AnalyticsEvent2(val name: String, val value: String)
data class Feat309AnalyticsEvent3(val name: String, val value: String)
data class Feat309AnalyticsEvent4(val name: String, val value: String)
data class Feat309AnalyticsEvent5(val name: String, val value: String)
data class Feat309AnalyticsEvent6(val name: String, val value: String)
data class Feat309AnalyticsEvent7(val name: String, val value: String)
data class Feat309AnalyticsEvent8(val name: String, val value: String)
data class Feat309AnalyticsEvent9(val name: String, val value: String)
data class Feat309AnalyticsEvent10(val name: String, val value: String)

fun logFeat309Event1(event: Feat309AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat309Event2(event: Feat309AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat309Event3(event: Feat309AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat309Event4(event: Feat309AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat309Event5(event: Feat309AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat309Event6(event: Feat309AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat309Event7(event: Feat309AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat309Event8(event: Feat309AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat309Event9(event: Feat309AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat309Event10(event: Feat309AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat309Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat309Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat309Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat309Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat309Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat309Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat309Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat309Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat309Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat309Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat309(u: CoreUser): Feat309Projection1 =
    Feat309Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat309Projection1> {
    val list = java.util.ArrayList<Feat309Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat309(u)
    }
    return list
}
