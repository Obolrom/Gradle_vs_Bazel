package com.romix.feature.feat162

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat162Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat162UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat162FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat162UserSummary
)

data class Feat162UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat162NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat162Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat162Config = Feat162Config()
) {

    fun loadSnapshot(userId: Long): Feat162NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat162NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat162UserSummary {
        return Feat162UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat162FeedItem> {
        val result = java.util.ArrayList<Feat162FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat162FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat162UiMapper {

    fun mapToUi(model: List<Feat162FeedItem>): Feat162UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat162UiModel(
            header = UiText("Feat162 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat162UiModel =
        Feat162UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat162UiModel =
        Feat162UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat162UiModel =
        Feat162UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat162Service(
    private val repository: Feat162Repository,
    private val uiMapper: Feat162UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat162UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat162UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat162UserItem1(val user: CoreUser, val label: String)
data class Feat162UserItem2(val user: CoreUser, val label: String)
data class Feat162UserItem3(val user: CoreUser, val label: String)
data class Feat162UserItem4(val user: CoreUser, val label: String)
data class Feat162UserItem5(val user: CoreUser, val label: String)
data class Feat162UserItem6(val user: CoreUser, val label: String)
data class Feat162UserItem7(val user: CoreUser, val label: String)
data class Feat162UserItem8(val user: CoreUser, val label: String)
data class Feat162UserItem9(val user: CoreUser, val label: String)
data class Feat162UserItem10(val user: CoreUser, val label: String)

data class Feat162StateBlock1(val state: Feat162UiModel, val checksum: Int)
data class Feat162StateBlock2(val state: Feat162UiModel, val checksum: Int)
data class Feat162StateBlock3(val state: Feat162UiModel, val checksum: Int)
data class Feat162StateBlock4(val state: Feat162UiModel, val checksum: Int)
data class Feat162StateBlock5(val state: Feat162UiModel, val checksum: Int)
data class Feat162StateBlock6(val state: Feat162UiModel, val checksum: Int)
data class Feat162StateBlock7(val state: Feat162UiModel, val checksum: Int)
data class Feat162StateBlock8(val state: Feat162UiModel, val checksum: Int)
data class Feat162StateBlock9(val state: Feat162UiModel, val checksum: Int)
data class Feat162StateBlock10(val state: Feat162UiModel, val checksum: Int)

fun buildFeat162UserItem(user: CoreUser, index: Int): Feat162UserItem1 {
    return Feat162UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat162StateBlock(model: Feat162UiModel): Feat162StateBlock1 {
    return Feat162StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat162UserSummary> {
    val list = java.util.ArrayList<Feat162UserSummary>(users.size)
    for (user in users) {
        list += Feat162UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat162UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat162UiModel {
    val summaries = (0 until count).map {
        Feat162UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat162UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat162UiModel> {
    val models = java.util.ArrayList<Feat162UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat162AnalyticsEvent1(val name: String, val value: String)
data class Feat162AnalyticsEvent2(val name: String, val value: String)
data class Feat162AnalyticsEvent3(val name: String, val value: String)
data class Feat162AnalyticsEvent4(val name: String, val value: String)
data class Feat162AnalyticsEvent5(val name: String, val value: String)
data class Feat162AnalyticsEvent6(val name: String, val value: String)
data class Feat162AnalyticsEvent7(val name: String, val value: String)
data class Feat162AnalyticsEvent8(val name: String, val value: String)
data class Feat162AnalyticsEvent9(val name: String, val value: String)
data class Feat162AnalyticsEvent10(val name: String, val value: String)

fun logFeat162Event1(event: Feat162AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat162Event2(event: Feat162AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat162Event3(event: Feat162AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat162Event4(event: Feat162AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat162Event5(event: Feat162AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat162Event6(event: Feat162AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat162Event7(event: Feat162AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat162Event8(event: Feat162AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat162Event9(event: Feat162AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat162Event10(event: Feat162AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat162Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat162Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat162Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat162Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat162Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat162Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat162Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat162Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat162Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat162Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat162(u: CoreUser): Feat162Projection1 =
    Feat162Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat162Projection1> {
    val list = java.util.ArrayList<Feat162Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat162(u)
    }
    return list
}
