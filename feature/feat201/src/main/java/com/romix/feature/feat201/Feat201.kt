package com.romix.feature.feat201

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat201Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat201UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat201FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat201UserSummary
)

data class Feat201UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat201NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat201Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat201Config = Feat201Config()
) {

    fun loadSnapshot(userId: Long): Feat201NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat201NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat201UserSummary {
        return Feat201UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat201FeedItem> {
        val result = java.util.ArrayList<Feat201FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat201FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat201UiMapper {

    fun mapToUi(model: List<Feat201FeedItem>): Feat201UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat201UiModel(
            header = UiText("Feat201 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat201UiModel =
        Feat201UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat201UiModel =
        Feat201UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat201UiModel =
        Feat201UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat201Service(
    private val repository: Feat201Repository,
    private val uiMapper: Feat201UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat201UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat201UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat201UserItem1(val user: CoreUser, val label: String)
data class Feat201UserItem2(val user: CoreUser, val label: String)
data class Feat201UserItem3(val user: CoreUser, val label: String)
data class Feat201UserItem4(val user: CoreUser, val label: String)
data class Feat201UserItem5(val user: CoreUser, val label: String)
data class Feat201UserItem6(val user: CoreUser, val label: String)
data class Feat201UserItem7(val user: CoreUser, val label: String)
data class Feat201UserItem8(val user: CoreUser, val label: String)
data class Feat201UserItem9(val user: CoreUser, val label: String)
data class Feat201UserItem10(val user: CoreUser, val label: String)

data class Feat201StateBlock1(val state: Feat201UiModel, val checksum: Int)
data class Feat201StateBlock2(val state: Feat201UiModel, val checksum: Int)
data class Feat201StateBlock3(val state: Feat201UiModel, val checksum: Int)
data class Feat201StateBlock4(val state: Feat201UiModel, val checksum: Int)
data class Feat201StateBlock5(val state: Feat201UiModel, val checksum: Int)
data class Feat201StateBlock6(val state: Feat201UiModel, val checksum: Int)
data class Feat201StateBlock7(val state: Feat201UiModel, val checksum: Int)
data class Feat201StateBlock8(val state: Feat201UiModel, val checksum: Int)
data class Feat201StateBlock9(val state: Feat201UiModel, val checksum: Int)
data class Feat201StateBlock10(val state: Feat201UiModel, val checksum: Int)

fun buildFeat201UserItem(user: CoreUser, index: Int): Feat201UserItem1 {
    return Feat201UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat201StateBlock(model: Feat201UiModel): Feat201StateBlock1 {
    return Feat201StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat201UserSummary> {
    val list = java.util.ArrayList<Feat201UserSummary>(users.size)
    for (user in users) {
        list += Feat201UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat201UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat201UiModel {
    val summaries = (0 until count).map {
        Feat201UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat201UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat201UiModel> {
    val models = java.util.ArrayList<Feat201UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat201AnalyticsEvent1(val name: String, val value: String)
data class Feat201AnalyticsEvent2(val name: String, val value: String)
data class Feat201AnalyticsEvent3(val name: String, val value: String)
data class Feat201AnalyticsEvent4(val name: String, val value: String)
data class Feat201AnalyticsEvent5(val name: String, val value: String)
data class Feat201AnalyticsEvent6(val name: String, val value: String)
data class Feat201AnalyticsEvent7(val name: String, val value: String)
data class Feat201AnalyticsEvent8(val name: String, val value: String)
data class Feat201AnalyticsEvent9(val name: String, val value: String)
data class Feat201AnalyticsEvent10(val name: String, val value: String)

fun logFeat201Event1(event: Feat201AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat201Event2(event: Feat201AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat201Event3(event: Feat201AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat201Event4(event: Feat201AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat201Event5(event: Feat201AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat201Event6(event: Feat201AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat201Event7(event: Feat201AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat201Event8(event: Feat201AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat201Event9(event: Feat201AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat201Event10(event: Feat201AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat201Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat201Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat201Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat201Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat201Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat201Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat201Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat201Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat201Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat201Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat201(u: CoreUser): Feat201Projection1 =
    Feat201Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat201Projection1> {
    val list = java.util.ArrayList<Feat201Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat201(u)
    }
    return list
}
