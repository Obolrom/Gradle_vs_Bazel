package com.romix.feature.feat40

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat40Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat40UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat40FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat40UserSummary
)

data class Feat40UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat40NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat40Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat40Config = Feat40Config()
) {

    fun loadSnapshot(userId: Long): Feat40NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat40NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat40UserSummary {
        return Feat40UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat40FeedItem> {
        val result = java.util.ArrayList<Feat40FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat40FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat40UiMapper {

    fun mapToUi(model: List<Feat40FeedItem>): Feat40UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat40UiModel(
            header = UiText("Feat40 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat40UiModel =
        Feat40UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat40UiModel =
        Feat40UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat40UiModel =
        Feat40UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat40Service(
    private val repository: Feat40Repository,
    private val uiMapper: Feat40UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat40UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat40UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat40UserItem1(val user: CoreUser, val label: String)
data class Feat40UserItem2(val user: CoreUser, val label: String)
data class Feat40UserItem3(val user: CoreUser, val label: String)
data class Feat40UserItem4(val user: CoreUser, val label: String)
data class Feat40UserItem5(val user: CoreUser, val label: String)
data class Feat40UserItem6(val user: CoreUser, val label: String)
data class Feat40UserItem7(val user: CoreUser, val label: String)
data class Feat40UserItem8(val user: CoreUser, val label: String)
data class Feat40UserItem9(val user: CoreUser, val label: String)
data class Feat40UserItem10(val user: CoreUser, val label: String)

data class Feat40StateBlock1(val state: Feat40UiModel, val checksum: Int)
data class Feat40StateBlock2(val state: Feat40UiModel, val checksum: Int)
data class Feat40StateBlock3(val state: Feat40UiModel, val checksum: Int)
data class Feat40StateBlock4(val state: Feat40UiModel, val checksum: Int)
data class Feat40StateBlock5(val state: Feat40UiModel, val checksum: Int)
data class Feat40StateBlock6(val state: Feat40UiModel, val checksum: Int)
data class Feat40StateBlock7(val state: Feat40UiModel, val checksum: Int)
data class Feat40StateBlock8(val state: Feat40UiModel, val checksum: Int)
data class Feat40StateBlock9(val state: Feat40UiModel, val checksum: Int)
data class Feat40StateBlock10(val state: Feat40UiModel, val checksum: Int)

fun buildFeat40UserItem(user: CoreUser, index: Int): Feat40UserItem1 {
    return Feat40UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat40StateBlock(model: Feat40UiModel): Feat40StateBlock1 {
    return Feat40StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat40UserSummary> {
    val list = java.util.ArrayList<Feat40UserSummary>(users.size)
    for (user in users) {
        list += Feat40UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat40UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat40UiModel {
    val summaries = (0 until count).map {
        Feat40UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat40UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat40UiModel> {
    val models = java.util.ArrayList<Feat40UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat40AnalyticsEvent1(val name: String, val value: String)
data class Feat40AnalyticsEvent2(val name: String, val value: String)
data class Feat40AnalyticsEvent3(val name: String, val value: String)
data class Feat40AnalyticsEvent4(val name: String, val value: String)
data class Feat40AnalyticsEvent5(val name: String, val value: String)
data class Feat40AnalyticsEvent6(val name: String, val value: String)
data class Feat40AnalyticsEvent7(val name: String, val value: String)
data class Feat40AnalyticsEvent8(val name: String, val value: String)
data class Feat40AnalyticsEvent9(val name: String, val value: String)
data class Feat40AnalyticsEvent10(val name: String, val value: String)

fun logFeat40Event1(event: Feat40AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat40Event2(event: Feat40AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat40Event3(event: Feat40AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat40Event4(event: Feat40AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat40Event5(event: Feat40AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat40Event6(event: Feat40AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat40Event7(event: Feat40AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat40Event8(event: Feat40AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat40Event9(event: Feat40AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat40Event10(event: Feat40AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat40Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat40Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat40Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat40Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat40Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat40Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat40Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat40Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat40Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat40Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat40(u: CoreUser): Feat40Projection1 =
    Feat40Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat40Projection1> {
    val list = java.util.ArrayList<Feat40Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat40(u)
    }
    return list
}
