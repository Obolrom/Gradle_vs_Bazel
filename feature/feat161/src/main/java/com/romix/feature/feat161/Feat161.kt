package com.romix.feature.feat161

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat161Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat161UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat161FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat161UserSummary
)

data class Feat161UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat161NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat161Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat161Config = Feat161Config()
) {

    fun loadSnapshot(userId: Long): Feat161NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat161NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat161UserSummary {
        return Feat161UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat161FeedItem> {
        val result = java.util.ArrayList<Feat161FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat161FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat161UiMapper {

    fun mapToUi(model: List<Feat161FeedItem>): Feat161UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat161UiModel(
            header = UiText("Feat161 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat161UiModel =
        Feat161UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat161UiModel =
        Feat161UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat161UiModel =
        Feat161UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat161Service(
    private val repository: Feat161Repository,
    private val uiMapper: Feat161UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat161UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat161UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat161UserItem1(val user: CoreUser, val label: String)
data class Feat161UserItem2(val user: CoreUser, val label: String)
data class Feat161UserItem3(val user: CoreUser, val label: String)
data class Feat161UserItem4(val user: CoreUser, val label: String)
data class Feat161UserItem5(val user: CoreUser, val label: String)
data class Feat161UserItem6(val user: CoreUser, val label: String)
data class Feat161UserItem7(val user: CoreUser, val label: String)
data class Feat161UserItem8(val user: CoreUser, val label: String)
data class Feat161UserItem9(val user: CoreUser, val label: String)
data class Feat161UserItem10(val user: CoreUser, val label: String)

data class Feat161StateBlock1(val state: Feat161UiModel, val checksum: Int)
data class Feat161StateBlock2(val state: Feat161UiModel, val checksum: Int)
data class Feat161StateBlock3(val state: Feat161UiModel, val checksum: Int)
data class Feat161StateBlock4(val state: Feat161UiModel, val checksum: Int)
data class Feat161StateBlock5(val state: Feat161UiModel, val checksum: Int)
data class Feat161StateBlock6(val state: Feat161UiModel, val checksum: Int)
data class Feat161StateBlock7(val state: Feat161UiModel, val checksum: Int)
data class Feat161StateBlock8(val state: Feat161UiModel, val checksum: Int)
data class Feat161StateBlock9(val state: Feat161UiModel, val checksum: Int)
data class Feat161StateBlock10(val state: Feat161UiModel, val checksum: Int)

fun buildFeat161UserItem(user: CoreUser, index: Int): Feat161UserItem1 {
    return Feat161UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat161StateBlock(model: Feat161UiModel): Feat161StateBlock1 {
    return Feat161StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat161UserSummary> {
    val list = java.util.ArrayList<Feat161UserSummary>(users.size)
    for (user in users) {
        list += Feat161UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat161UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat161UiModel {
    val summaries = (0 until count).map {
        Feat161UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat161UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat161UiModel> {
    val models = java.util.ArrayList<Feat161UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat161AnalyticsEvent1(val name: String, val value: String)
data class Feat161AnalyticsEvent2(val name: String, val value: String)
data class Feat161AnalyticsEvent3(val name: String, val value: String)
data class Feat161AnalyticsEvent4(val name: String, val value: String)
data class Feat161AnalyticsEvent5(val name: String, val value: String)
data class Feat161AnalyticsEvent6(val name: String, val value: String)
data class Feat161AnalyticsEvent7(val name: String, val value: String)
data class Feat161AnalyticsEvent8(val name: String, val value: String)
data class Feat161AnalyticsEvent9(val name: String, val value: String)
data class Feat161AnalyticsEvent10(val name: String, val value: String)

fun logFeat161Event1(event: Feat161AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat161Event2(event: Feat161AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat161Event3(event: Feat161AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat161Event4(event: Feat161AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat161Event5(event: Feat161AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat161Event6(event: Feat161AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat161Event7(event: Feat161AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat161Event8(event: Feat161AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat161Event9(event: Feat161AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat161Event10(event: Feat161AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat161Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat161Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat161Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat161Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat161Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat161Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat161Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat161Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat161Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat161Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat161(u: CoreUser): Feat161Projection1 =
    Feat161Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat161Projection1> {
    val list = java.util.ArrayList<Feat161Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat161(u)
    }
    return list
}
