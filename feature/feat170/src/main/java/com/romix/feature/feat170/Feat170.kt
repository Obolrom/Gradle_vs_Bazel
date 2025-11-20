package com.romix.feature.feat170

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat170Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat170UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat170FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat170UserSummary
)

data class Feat170UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat170NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat170Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat170Config = Feat170Config()
) {

    fun loadSnapshot(userId: Long): Feat170NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat170NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat170UserSummary {
        return Feat170UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat170FeedItem> {
        val result = java.util.ArrayList<Feat170FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat170FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat170UiMapper {

    fun mapToUi(model: List<Feat170FeedItem>): Feat170UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat170UiModel(
            header = UiText("Feat170 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat170UiModel =
        Feat170UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat170UiModel =
        Feat170UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat170UiModel =
        Feat170UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat170Service(
    private val repository: Feat170Repository,
    private val uiMapper: Feat170UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat170UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat170UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat170UserItem1(val user: CoreUser, val label: String)
data class Feat170UserItem2(val user: CoreUser, val label: String)
data class Feat170UserItem3(val user: CoreUser, val label: String)
data class Feat170UserItem4(val user: CoreUser, val label: String)
data class Feat170UserItem5(val user: CoreUser, val label: String)
data class Feat170UserItem6(val user: CoreUser, val label: String)
data class Feat170UserItem7(val user: CoreUser, val label: String)
data class Feat170UserItem8(val user: CoreUser, val label: String)
data class Feat170UserItem9(val user: CoreUser, val label: String)
data class Feat170UserItem10(val user: CoreUser, val label: String)

data class Feat170StateBlock1(val state: Feat170UiModel, val checksum: Int)
data class Feat170StateBlock2(val state: Feat170UiModel, val checksum: Int)
data class Feat170StateBlock3(val state: Feat170UiModel, val checksum: Int)
data class Feat170StateBlock4(val state: Feat170UiModel, val checksum: Int)
data class Feat170StateBlock5(val state: Feat170UiModel, val checksum: Int)
data class Feat170StateBlock6(val state: Feat170UiModel, val checksum: Int)
data class Feat170StateBlock7(val state: Feat170UiModel, val checksum: Int)
data class Feat170StateBlock8(val state: Feat170UiModel, val checksum: Int)
data class Feat170StateBlock9(val state: Feat170UiModel, val checksum: Int)
data class Feat170StateBlock10(val state: Feat170UiModel, val checksum: Int)

fun buildFeat170UserItem(user: CoreUser, index: Int): Feat170UserItem1 {
    return Feat170UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat170StateBlock(model: Feat170UiModel): Feat170StateBlock1 {
    return Feat170StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat170UserSummary> {
    val list = java.util.ArrayList<Feat170UserSummary>(users.size)
    for (user in users) {
        list += Feat170UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat170UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat170UiModel {
    val summaries = (0 until count).map {
        Feat170UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat170UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat170UiModel> {
    val models = java.util.ArrayList<Feat170UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat170AnalyticsEvent1(val name: String, val value: String)
data class Feat170AnalyticsEvent2(val name: String, val value: String)
data class Feat170AnalyticsEvent3(val name: String, val value: String)
data class Feat170AnalyticsEvent4(val name: String, val value: String)
data class Feat170AnalyticsEvent5(val name: String, val value: String)
data class Feat170AnalyticsEvent6(val name: String, val value: String)
data class Feat170AnalyticsEvent7(val name: String, val value: String)
data class Feat170AnalyticsEvent8(val name: String, val value: String)
data class Feat170AnalyticsEvent9(val name: String, val value: String)
data class Feat170AnalyticsEvent10(val name: String, val value: String)

fun logFeat170Event1(event: Feat170AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat170Event2(event: Feat170AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat170Event3(event: Feat170AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat170Event4(event: Feat170AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat170Event5(event: Feat170AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat170Event6(event: Feat170AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat170Event7(event: Feat170AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat170Event8(event: Feat170AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat170Event9(event: Feat170AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat170Event10(event: Feat170AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat170Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat170Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat170Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat170Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat170Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat170Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat170Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat170Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat170Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat170Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat170(u: CoreUser): Feat170Projection1 =
    Feat170Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat170Projection1> {
    val list = java.util.ArrayList<Feat170Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat170(u)
    }
    return list
}
