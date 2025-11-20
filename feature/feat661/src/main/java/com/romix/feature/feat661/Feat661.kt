package com.romix.feature.feat661

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat661Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat661UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat661FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat661UserSummary
)

data class Feat661UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat661NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat661Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat661Config = Feat661Config()
) {

    fun loadSnapshot(userId: Long): Feat661NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat661NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat661UserSummary {
        return Feat661UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat661FeedItem> {
        val result = java.util.ArrayList<Feat661FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat661FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat661UiMapper {

    fun mapToUi(model: List<Feat661FeedItem>): Feat661UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat661UiModel(
            header = UiText("Feat661 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat661UiModel =
        Feat661UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat661UiModel =
        Feat661UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat661UiModel =
        Feat661UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat661Service(
    private val repository: Feat661Repository,
    private val uiMapper: Feat661UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat661UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat661UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat661UserItem1(val user: CoreUser, val label: String)
data class Feat661UserItem2(val user: CoreUser, val label: String)
data class Feat661UserItem3(val user: CoreUser, val label: String)
data class Feat661UserItem4(val user: CoreUser, val label: String)
data class Feat661UserItem5(val user: CoreUser, val label: String)
data class Feat661UserItem6(val user: CoreUser, val label: String)
data class Feat661UserItem7(val user: CoreUser, val label: String)
data class Feat661UserItem8(val user: CoreUser, val label: String)
data class Feat661UserItem9(val user: CoreUser, val label: String)
data class Feat661UserItem10(val user: CoreUser, val label: String)

data class Feat661StateBlock1(val state: Feat661UiModel, val checksum: Int)
data class Feat661StateBlock2(val state: Feat661UiModel, val checksum: Int)
data class Feat661StateBlock3(val state: Feat661UiModel, val checksum: Int)
data class Feat661StateBlock4(val state: Feat661UiModel, val checksum: Int)
data class Feat661StateBlock5(val state: Feat661UiModel, val checksum: Int)
data class Feat661StateBlock6(val state: Feat661UiModel, val checksum: Int)
data class Feat661StateBlock7(val state: Feat661UiModel, val checksum: Int)
data class Feat661StateBlock8(val state: Feat661UiModel, val checksum: Int)
data class Feat661StateBlock9(val state: Feat661UiModel, val checksum: Int)
data class Feat661StateBlock10(val state: Feat661UiModel, val checksum: Int)

fun buildFeat661UserItem(user: CoreUser, index: Int): Feat661UserItem1 {
    return Feat661UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat661StateBlock(model: Feat661UiModel): Feat661StateBlock1 {
    return Feat661StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat661UserSummary> {
    val list = java.util.ArrayList<Feat661UserSummary>(users.size)
    for (user in users) {
        list += Feat661UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat661UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat661UiModel {
    val summaries = (0 until count).map {
        Feat661UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat661UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat661UiModel> {
    val models = java.util.ArrayList<Feat661UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat661AnalyticsEvent1(val name: String, val value: String)
data class Feat661AnalyticsEvent2(val name: String, val value: String)
data class Feat661AnalyticsEvent3(val name: String, val value: String)
data class Feat661AnalyticsEvent4(val name: String, val value: String)
data class Feat661AnalyticsEvent5(val name: String, val value: String)
data class Feat661AnalyticsEvent6(val name: String, val value: String)
data class Feat661AnalyticsEvent7(val name: String, val value: String)
data class Feat661AnalyticsEvent8(val name: String, val value: String)
data class Feat661AnalyticsEvent9(val name: String, val value: String)
data class Feat661AnalyticsEvent10(val name: String, val value: String)

fun logFeat661Event1(event: Feat661AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat661Event2(event: Feat661AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat661Event3(event: Feat661AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat661Event4(event: Feat661AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat661Event5(event: Feat661AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat661Event6(event: Feat661AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat661Event7(event: Feat661AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat661Event8(event: Feat661AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat661Event9(event: Feat661AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat661Event10(event: Feat661AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat661Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat661Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat661Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat661Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat661Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat661Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat661Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat661Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat661Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat661Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat661(u: CoreUser): Feat661Projection1 =
    Feat661Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat661Projection1> {
    val list = java.util.ArrayList<Feat661Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat661(u)
    }
    return list
}
