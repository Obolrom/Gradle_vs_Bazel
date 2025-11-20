package com.romix.feature.feat245

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat245Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat245UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat245FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat245UserSummary
)

data class Feat245UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat245NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat245Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat245Config = Feat245Config()
) {

    fun loadSnapshot(userId: Long): Feat245NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat245NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat245UserSummary {
        return Feat245UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat245FeedItem> {
        val result = java.util.ArrayList<Feat245FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat245FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat245UiMapper {

    fun mapToUi(model: List<Feat245FeedItem>): Feat245UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat245UiModel(
            header = UiText("Feat245 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat245UiModel =
        Feat245UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat245UiModel =
        Feat245UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat245UiModel =
        Feat245UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat245Service(
    private val repository: Feat245Repository,
    private val uiMapper: Feat245UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat245UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat245UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat245UserItem1(val user: CoreUser, val label: String)
data class Feat245UserItem2(val user: CoreUser, val label: String)
data class Feat245UserItem3(val user: CoreUser, val label: String)
data class Feat245UserItem4(val user: CoreUser, val label: String)
data class Feat245UserItem5(val user: CoreUser, val label: String)
data class Feat245UserItem6(val user: CoreUser, val label: String)
data class Feat245UserItem7(val user: CoreUser, val label: String)
data class Feat245UserItem8(val user: CoreUser, val label: String)
data class Feat245UserItem9(val user: CoreUser, val label: String)
data class Feat245UserItem10(val user: CoreUser, val label: String)

data class Feat245StateBlock1(val state: Feat245UiModel, val checksum: Int)
data class Feat245StateBlock2(val state: Feat245UiModel, val checksum: Int)
data class Feat245StateBlock3(val state: Feat245UiModel, val checksum: Int)
data class Feat245StateBlock4(val state: Feat245UiModel, val checksum: Int)
data class Feat245StateBlock5(val state: Feat245UiModel, val checksum: Int)
data class Feat245StateBlock6(val state: Feat245UiModel, val checksum: Int)
data class Feat245StateBlock7(val state: Feat245UiModel, val checksum: Int)
data class Feat245StateBlock8(val state: Feat245UiModel, val checksum: Int)
data class Feat245StateBlock9(val state: Feat245UiModel, val checksum: Int)
data class Feat245StateBlock10(val state: Feat245UiModel, val checksum: Int)

fun buildFeat245UserItem(user: CoreUser, index: Int): Feat245UserItem1 {
    return Feat245UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat245StateBlock(model: Feat245UiModel): Feat245StateBlock1 {
    return Feat245StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat245UserSummary> {
    val list = java.util.ArrayList<Feat245UserSummary>(users.size)
    for (user in users) {
        list += Feat245UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat245UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat245UiModel {
    val summaries = (0 until count).map {
        Feat245UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat245UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat245UiModel> {
    val models = java.util.ArrayList<Feat245UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat245AnalyticsEvent1(val name: String, val value: String)
data class Feat245AnalyticsEvent2(val name: String, val value: String)
data class Feat245AnalyticsEvent3(val name: String, val value: String)
data class Feat245AnalyticsEvent4(val name: String, val value: String)
data class Feat245AnalyticsEvent5(val name: String, val value: String)
data class Feat245AnalyticsEvent6(val name: String, val value: String)
data class Feat245AnalyticsEvent7(val name: String, val value: String)
data class Feat245AnalyticsEvent8(val name: String, val value: String)
data class Feat245AnalyticsEvent9(val name: String, val value: String)
data class Feat245AnalyticsEvent10(val name: String, val value: String)

fun logFeat245Event1(event: Feat245AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat245Event2(event: Feat245AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat245Event3(event: Feat245AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat245Event4(event: Feat245AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat245Event5(event: Feat245AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat245Event6(event: Feat245AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat245Event7(event: Feat245AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat245Event8(event: Feat245AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat245Event9(event: Feat245AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat245Event10(event: Feat245AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat245Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat245Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat245Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat245Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat245Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat245Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat245Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat245Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat245Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat245Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat245(u: CoreUser): Feat245Projection1 =
    Feat245Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat245Projection1> {
    val list = java.util.ArrayList<Feat245Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat245(u)
    }
    return list
}
