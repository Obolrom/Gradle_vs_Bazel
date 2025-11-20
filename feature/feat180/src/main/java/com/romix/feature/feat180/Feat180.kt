package com.romix.feature.feat180

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat180Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat180UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat180FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat180UserSummary
)

data class Feat180UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat180NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat180Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat180Config = Feat180Config()
) {

    fun loadSnapshot(userId: Long): Feat180NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat180NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat180UserSummary {
        return Feat180UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat180FeedItem> {
        val result = java.util.ArrayList<Feat180FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat180FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat180UiMapper {

    fun mapToUi(model: List<Feat180FeedItem>): Feat180UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat180UiModel(
            header = UiText("Feat180 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat180UiModel =
        Feat180UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat180UiModel =
        Feat180UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat180UiModel =
        Feat180UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat180Service(
    private val repository: Feat180Repository,
    private val uiMapper: Feat180UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat180UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat180UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat180UserItem1(val user: CoreUser, val label: String)
data class Feat180UserItem2(val user: CoreUser, val label: String)
data class Feat180UserItem3(val user: CoreUser, val label: String)
data class Feat180UserItem4(val user: CoreUser, val label: String)
data class Feat180UserItem5(val user: CoreUser, val label: String)
data class Feat180UserItem6(val user: CoreUser, val label: String)
data class Feat180UserItem7(val user: CoreUser, val label: String)
data class Feat180UserItem8(val user: CoreUser, val label: String)
data class Feat180UserItem9(val user: CoreUser, val label: String)
data class Feat180UserItem10(val user: CoreUser, val label: String)

data class Feat180StateBlock1(val state: Feat180UiModel, val checksum: Int)
data class Feat180StateBlock2(val state: Feat180UiModel, val checksum: Int)
data class Feat180StateBlock3(val state: Feat180UiModel, val checksum: Int)
data class Feat180StateBlock4(val state: Feat180UiModel, val checksum: Int)
data class Feat180StateBlock5(val state: Feat180UiModel, val checksum: Int)
data class Feat180StateBlock6(val state: Feat180UiModel, val checksum: Int)
data class Feat180StateBlock7(val state: Feat180UiModel, val checksum: Int)
data class Feat180StateBlock8(val state: Feat180UiModel, val checksum: Int)
data class Feat180StateBlock9(val state: Feat180UiModel, val checksum: Int)
data class Feat180StateBlock10(val state: Feat180UiModel, val checksum: Int)

fun buildFeat180UserItem(user: CoreUser, index: Int): Feat180UserItem1 {
    return Feat180UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat180StateBlock(model: Feat180UiModel): Feat180StateBlock1 {
    return Feat180StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat180UserSummary> {
    val list = java.util.ArrayList<Feat180UserSummary>(users.size)
    for (user in users) {
        list += Feat180UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat180UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat180UiModel {
    val summaries = (0 until count).map {
        Feat180UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat180UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat180UiModel> {
    val models = java.util.ArrayList<Feat180UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat180AnalyticsEvent1(val name: String, val value: String)
data class Feat180AnalyticsEvent2(val name: String, val value: String)
data class Feat180AnalyticsEvent3(val name: String, val value: String)
data class Feat180AnalyticsEvent4(val name: String, val value: String)
data class Feat180AnalyticsEvent5(val name: String, val value: String)
data class Feat180AnalyticsEvent6(val name: String, val value: String)
data class Feat180AnalyticsEvent7(val name: String, val value: String)
data class Feat180AnalyticsEvent8(val name: String, val value: String)
data class Feat180AnalyticsEvent9(val name: String, val value: String)
data class Feat180AnalyticsEvent10(val name: String, val value: String)

fun logFeat180Event1(event: Feat180AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat180Event2(event: Feat180AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat180Event3(event: Feat180AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat180Event4(event: Feat180AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat180Event5(event: Feat180AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat180Event6(event: Feat180AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat180Event7(event: Feat180AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat180Event8(event: Feat180AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat180Event9(event: Feat180AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat180Event10(event: Feat180AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat180Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat180Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat180Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat180Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat180Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat180Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat180Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat180Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat180Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat180Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat180(u: CoreUser): Feat180Projection1 =
    Feat180Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat180Projection1> {
    val list = java.util.ArrayList<Feat180Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat180(u)
    }
    return list
}
