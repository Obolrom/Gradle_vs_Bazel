package com.romix.feature.feat600

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat600Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat600UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat600FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat600UserSummary
)

data class Feat600UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat600NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat600Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat600Config = Feat600Config()
) {

    fun loadSnapshot(userId: Long): Feat600NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat600NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat600UserSummary {
        return Feat600UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat600FeedItem> {
        val result = java.util.ArrayList<Feat600FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat600FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat600UiMapper {

    fun mapToUi(model: List<Feat600FeedItem>): Feat600UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat600UiModel(
            header = UiText("Feat600 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat600UiModel =
        Feat600UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat600UiModel =
        Feat600UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat600UiModel =
        Feat600UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat600Service(
    private val repository: Feat600Repository,
    private val uiMapper: Feat600UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat600UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat600UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat600UserItem1(val user: CoreUser, val label: String)
data class Feat600UserItem2(val user: CoreUser, val label: String)
data class Feat600UserItem3(val user: CoreUser, val label: String)
data class Feat600UserItem4(val user: CoreUser, val label: String)
data class Feat600UserItem5(val user: CoreUser, val label: String)
data class Feat600UserItem6(val user: CoreUser, val label: String)
data class Feat600UserItem7(val user: CoreUser, val label: String)
data class Feat600UserItem8(val user: CoreUser, val label: String)
data class Feat600UserItem9(val user: CoreUser, val label: String)
data class Feat600UserItem10(val user: CoreUser, val label: String)

data class Feat600StateBlock1(val state: Feat600UiModel, val checksum: Int)
data class Feat600StateBlock2(val state: Feat600UiModel, val checksum: Int)
data class Feat600StateBlock3(val state: Feat600UiModel, val checksum: Int)
data class Feat600StateBlock4(val state: Feat600UiModel, val checksum: Int)
data class Feat600StateBlock5(val state: Feat600UiModel, val checksum: Int)
data class Feat600StateBlock6(val state: Feat600UiModel, val checksum: Int)
data class Feat600StateBlock7(val state: Feat600UiModel, val checksum: Int)
data class Feat600StateBlock8(val state: Feat600UiModel, val checksum: Int)
data class Feat600StateBlock9(val state: Feat600UiModel, val checksum: Int)
data class Feat600StateBlock10(val state: Feat600UiModel, val checksum: Int)

fun buildFeat600UserItem(user: CoreUser, index: Int): Feat600UserItem1 {
    return Feat600UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat600StateBlock(model: Feat600UiModel): Feat600StateBlock1 {
    return Feat600StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat600UserSummary> {
    val list = java.util.ArrayList<Feat600UserSummary>(users.size)
    for (user in users) {
        list += Feat600UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat600UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat600UiModel {
    val summaries = (0 until count).map {
        Feat600UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat600UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat600UiModel> {
    val models = java.util.ArrayList<Feat600UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat600AnalyticsEvent1(val name: String, val value: String)
data class Feat600AnalyticsEvent2(val name: String, val value: String)
data class Feat600AnalyticsEvent3(val name: String, val value: String)
data class Feat600AnalyticsEvent4(val name: String, val value: String)
data class Feat600AnalyticsEvent5(val name: String, val value: String)
data class Feat600AnalyticsEvent6(val name: String, val value: String)
data class Feat600AnalyticsEvent7(val name: String, val value: String)
data class Feat600AnalyticsEvent8(val name: String, val value: String)
data class Feat600AnalyticsEvent9(val name: String, val value: String)
data class Feat600AnalyticsEvent10(val name: String, val value: String)

fun logFeat600Event1(event: Feat600AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat600Event2(event: Feat600AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat600Event3(event: Feat600AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat600Event4(event: Feat600AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat600Event5(event: Feat600AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat600Event6(event: Feat600AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat600Event7(event: Feat600AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat600Event8(event: Feat600AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat600Event9(event: Feat600AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat600Event10(event: Feat600AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat600Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat600Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat600Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat600Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat600Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat600Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat600Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat600Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat600Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat600Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat600(u: CoreUser): Feat600Projection1 =
    Feat600Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat600Projection1> {
    val list = java.util.ArrayList<Feat600Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat600(u)
    }
    return list
}
