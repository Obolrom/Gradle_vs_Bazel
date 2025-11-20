package com.romix.feature.feat372

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat372Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat372UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat372FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat372UserSummary
)

data class Feat372UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat372NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat372Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat372Config = Feat372Config()
) {

    fun loadSnapshot(userId: Long): Feat372NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat372NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat372UserSummary {
        return Feat372UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat372FeedItem> {
        val result = java.util.ArrayList<Feat372FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat372FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat372UiMapper {

    fun mapToUi(model: List<Feat372FeedItem>): Feat372UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat372UiModel(
            header = UiText("Feat372 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat372UiModel =
        Feat372UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat372UiModel =
        Feat372UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat372UiModel =
        Feat372UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat372Service(
    private val repository: Feat372Repository,
    private val uiMapper: Feat372UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat372UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat372UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat372UserItem1(val user: CoreUser, val label: String)
data class Feat372UserItem2(val user: CoreUser, val label: String)
data class Feat372UserItem3(val user: CoreUser, val label: String)
data class Feat372UserItem4(val user: CoreUser, val label: String)
data class Feat372UserItem5(val user: CoreUser, val label: String)
data class Feat372UserItem6(val user: CoreUser, val label: String)
data class Feat372UserItem7(val user: CoreUser, val label: String)
data class Feat372UserItem8(val user: CoreUser, val label: String)
data class Feat372UserItem9(val user: CoreUser, val label: String)
data class Feat372UserItem10(val user: CoreUser, val label: String)

data class Feat372StateBlock1(val state: Feat372UiModel, val checksum: Int)
data class Feat372StateBlock2(val state: Feat372UiModel, val checksum: Int)
data class Feat372StateBlock3(val state: Feat372UiModel, val checksum: Int)
data class Feat372StateBlock4(val state: Feat372UiModel, val checksum: Int)
data class Feat372StateBlock5(val state: Feat372UiModel, val checksum: Int)
data class Feat372StateBlock6(val state: Feat372UiModel, val checksum: Int)
data class Feat372StateBlock7(val state: Feat372UiModel, val checksum: Int)
data class Feat372StateBlock8(val state: Feat372UiModel, val checksum: Int)
data class Feat372StateBlock9(val state: Feat372UiModel, val checksum: Int)
data class Feat372StateBlock10(val state: Feat372UiModel, val checksum: Int)

fun buildFeat372UserItem(user: CoreUser, index: Int): Feat372UserItem1 {
    return Feat372UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat372StateBlock(model: Feat372UiModel): Feat372StateBlock1 {
    return Feat372StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat372UserSummary> {
    val list = java.util.ArrayList<Feat372UserSummary>(users.size)
    for (user in users) {
        list += Feat372UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat372UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat372UiModel {
    val summaries = (0 until count).map {
        Feat372UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat372UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat372UiModel> {
    val models = java.util.ArrayList<Feat372UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat372AnalyticsEvent1(val name: String, val value: String)
data class Feat372AnalyticsEvent2(val name: String, val value: String)
data class Feat372AnalyticsEvent3(val name: String, val value: String)
data class Feat372AnalyticsEvent4(val name: String, val value: String)
data class Feat372AnalyticsEvent5(val name: String, val value: String)
data class Feat372AnalyticsEvent6(val name: String, val value: String)
data class Feat372AnalyticsEvent7(val name: String, val value: String)
data class Feat372AnalyticsEvent8(val name: String, val value: String)
data class Feat372AnalyticsEvent9(val name: String, val value: String)
data class Feat372AnalyticsEvent10(val name: String, val value: String)

fun logFeat372Event1(event: Feat372AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat372Event2(event: Feat372AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat372Event3(event: Feat372AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat372Event4(event: Feat372AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat372Event5(event: Feat372AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat372Event6(event: Feat372AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat372Event7(event: Feat372AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat372Event8(event: Feat372AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat372Event9(event: Feat372AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat372Event10(event: Feat372AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat372Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat372Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat372Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat372Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat372Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat372Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat372Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat372Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat372Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat372Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat372(u: CoreUser): Feat372Projection1 =
    Feat372Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat372Projection1> {
    val list = java.util.ArrayList<Feat372Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat372(u)
    }
    return list
}
