package com.romix.feature.feat585

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat585Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat585UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat585FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat585UserSummary
)

data class Feat585UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat585NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat585Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat585Config = Feat585Config()
) {

    fun loadSnapshot(userId: Long): Feat585NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat585NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat585UserSummary {
        return Feat585UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat585FeedItem> {
        val result = java.util.ArrayList<Feat585FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat585FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat585UiMapper {

    fun mapToUi(model: List<Feat585FeedItem>): Feat585UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat585UiModel(
            header = UiText("Feat585 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat585UiModel =
        Feat585UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat585UiModel =
        Feat585UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat585UiModel =
        Feat585UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat585Service(
    private val repository: Feat585Repository,
    private val uiMapper: Feat585UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat585UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat585UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat585UserItem1(val user: CoreUser, val label: String)
data class Feat585UserItem2(val user: CoreUser, val label: String)
data class Feat585UserItem3(val user: CoreUser, val label: String)
data class Feat585UserItem4(val user: CoreUser, val label: String)
data class Feat585UserItem5(val user: CoreUser, val label: String)
data class Feat585UserItem6(val user: CoreUser, val label: String)
data class Feat585UserItem7(val user: CoreUser, val label: String)
data class Feat585UserItem8(val user: CoreUser, val label: String)
data class Feat585UserItem9(val user: CoreUser, val label: String)
data class Feat585UserItem10(val user: CoreUser, val label: String)

data class Feat585StateBlock1(val state: Feat585UiModel, val checksum: Int)
data class Feat585StateBlock2(val state: Feat585UiModel, val checksum: Int)
data class Feat585StateBlock3(val state: Feat585UiModel, val checksum: Int)
data class Feat585StateBlock4(val state: Feat585UiModel, val checksum: Int)
data class Feat585StateBlock5(val state: Feat585UiModel, val checksum: Int)
data class Feat585StateBlock6(val state: Feat585UiModel, val checksum: Int)
data class Feat585StateBlock7(val state: Feat585UiModel, val checksum: Int)
data class Feat585StateBlock8(val state: Feat585UiModel, val checksum: Int)
data class Feat585StateBlock9(val state: Feat585UiModel, val checksum: Int)
data class Feat585StateBlock10(val state: Feat585UiModel, val checksum: Int)

fun buildFeat585UserItem(user: CoreUser, index: Int): Feat585UserItem1 {
    return Feat585UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat585StateBlock(model: Feat585UiModel): Feat585StateBlock1 {
    return Feat585StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat585UserSummary> {
    val list = java.util.ArrayList<Feat585UserSummary>(users.size)
    for (user in users) {
        list += Feat585UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat585UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat585UiModel {
    val summaries = (0 until count).map {
        Feat585UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat585UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat585UiModel> {
    val models = java.util.ArrayList<Feat585UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat585AnalyticsEvent1(val name: String, val value: String)
data class Feat585AnalyticsEvent2(val name: String, val value: String)
data class Feat585AnalyticsEvent3(val name: String, val value: String)
data class Feat585AnalyticsEvent4(val name: String, val value: String)
data class Feat585AnalyticsEvent5(val name: String, val value: String)
data class Feat585AnalyticsEvent6(val name: String, val value: String)
data class Feat585AnalyticsEvent7(val name: String, val value: String)
data class Feat585AnalyticsEvent8(val name: String, val value: String)
data class Feat585AnalyticsEvent9(val name: String, val value: String)
data class Feat585AnalyticsEvent10(val name: String, val value: String)

fun logFeat585Event1(event: Feat585AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat585Event2(event: Feat585AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat585Event3(event: Feat585AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat585Event4(event: Feat585AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat585Event5(event: Feat585AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat585Event6(event: Feat585AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat585Event7(event: Feat585AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat585Event8(event: Feat585AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat585Event9(event: Feat585AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat585Event10(event: Feat585AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat585Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat585Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat585Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat585Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat585Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat585Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat585Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat585Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat585Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat585Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat585(u: CoreUser): Feat585Projection1 =
    Feat585Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat585Projection1> {
    val list = java.util.ArrayList<Feat585Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat585(u)
    }
    return list
}
