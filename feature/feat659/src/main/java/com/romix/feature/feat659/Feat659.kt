package com.romix.feature.feat659

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat659Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat659UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat659FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat659UserSummary
)

data class Feat659UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat659NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat659Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat659Config = Feat659Config()
) {

    fun loadSnapshot(userId: Long): Feat659NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat659NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat659UserSummary {
        return Feat659UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat659FeedItem> {
        val result = java.util.ArrayList<Feat659FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat659FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat659UiMapper {

    fun mapToUi(model: List<Feat659FeedItem>): Feat659UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat659UiModel(
            header = UiText("Feat659 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat659UiModel =
        Feat659UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat659UiModel =
        Feat659UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat659UiModel =
        Feat659UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat659Service(
    private val repository: Feat659Repository,
    private val uiMapper: Feat659UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat659UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat659UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat659UserItem1(val user: CoreUser, val label: String)
data class Feat659UserItem2(val user: CoreUser, val label: String)
data class Feat659UserItem3(val user: CoreUser, val label: String)
data class Feat659UserItem4(val user: CoreUser, val label: String)
data class Feat659UserItem5(val user: CoreUser, val label: String)
data class Feat659UserItem6(val user: CoreUser, val label: String)
data class Feat659UserItem7(val user: CoreUser, val label: String)
data class Feat659UserItem8(val user: CoreUser, val label: String)
data class Feat659UserItem9(val user: CoreUser, val label: String)
data class Feat659UserItem10(val user: CoreUser, val label: String)

data class Feat659StateBlock1(val state: Feat659UiModel, val checksum: Int)
data class Feat659StateBlock2(val state: Feat659UiModel, val checksum: Int)
data class Feat659StateBlock3(val state: Feat659UiModel, val checksum: Int)
data class Feat659StateBlock4(val state: Feat659UiModel, val checksum: Int)
data class Feat659StateBlock5(val state: Feat659UiModel, val checksum: Int)
data class Feat659StateBlock6(val state: Feat659UiModel, val checksum: Int)
data class Feat659StateBlock7(val state: Feat659UiModel, val checksum: Int)
data class Feat659StateBlock8(val state: Feat659UiModel, val checksum: Int)
data class Feat659StateBlock9(val state: Feat659UiModel, val checksum: Int)
data class Feat659StateBlock10(val state: Feat659UiModel, val checksum: Int)

fun buildFeat659UserItem(user: CoreUser, index: Int): Feat659UserItem1 {
    return Feat659UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat659StateBlock(model: Feat659UiModel): Feat659StateBlock1 {
    return Feat659StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat659UserSummary> {
    val list = java.util.ArrayList<Feat659UserSummary>(users.size)
    for (user in users) {
        list += Feat659UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat659UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat659UiModel {
    val summaries = (0 until count).map {
        Feat659UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat659UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat659UiModel> {
    val models = java.util.ArrayList<Feat659UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat659AnalyticsEvent1(val name: String, val value: String)
data class Feat659AnalyticsEvent2(val name: String, val value: String)
data class Feat659AnalyticsEvent3(val name: String, val value: String)
data class Feat659AnalyticsEvent4(val name: String, val value: String)
data class Feat659AnalyticsEvent5(val name: String, val value: String)
data class Feat659AnalyticsEvent6(val name: String, val value: String)
data class Feat659AnalyticsEvent7(val name: String, val value: String)
data class Feat659AnalyticsEvent8(val name: String, val value: String)
data class Feat659AnalyticsEvent9(val name: String, val value: String)
data class Feat659AnalyticsEvent10(val name: String, val value: String)

fun logFeat659Event1(event: Feat659AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat659Event2(event: Feat659AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat659Event3(event: Feat659AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat659Event4(event: Feat659AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat659Event5(event: Feat659AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat659Event6(event: Feat659AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat659Event7(event: Feat659AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat659Event8(event: Feat659AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat659Event9(event: Feat659AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat659Event10(event: Feat659AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat659Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat659Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat659Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat659Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat659Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat659Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat659Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat659Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat659Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat659Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat659(u: CoreUser): Feat659Projection1 =
    Feat659Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat659Projection1> {
    val list = java.util.ArrayList<Feat659Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat659(u)
    }
    return list
}
