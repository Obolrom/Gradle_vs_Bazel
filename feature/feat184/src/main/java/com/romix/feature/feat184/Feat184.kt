package com.romix.feature.feat184

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat184Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat184UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat184FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat184UserSummary
)

data class Feat184UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat184NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat184Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat184Config = Feat184Config()
) {

    fun loadSnapshot(userId: Long): Feat184NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat184NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat184UserSummary {
        return Feat184UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat184FeedItem> {
        val result = java.util.ArrayList<Feat184FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat184FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat184UiMapper {

    fun mapToUi(model: List<Feat184FeedItem>): Feat184UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat184UiModel(
            header = UiText("Feat184 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat184UiModel =
        Feat184UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat184UiModel =
        Feat184UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat184UiModel =
        Feat184UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat184Service(
    private val repository: Feat184Repository,
    private val uiMapper: Feat184UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat184UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat184UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat184UserItem1(val user: CoreUser, val label: String)
data class Feat184UserItem2(val user: CoreUser, val label: String)
data class Feat184UserItem3(val user: CoreUser, val label: String)
data class Feat184UserItem4(val user: CoreUser, val label: String)
data class Feat184UserItem5(val user: CoreUser, val label: String)
data class Feat184UserItem6(val user: CoreUser, val label: String)
data class Feat184UserItem7(val user: CoreUser, val label: String)
data class Feat184UserItem8(val user: CoreUser, val label: String)
data class Feat184UserItem9(val user: CoreUser, val label: String)
data class Feat184UserItem10(val user: CoreUser, val label: String)

data class Feat184StateBlock1(val state: Feat184UiModel, val checksum: Int)
data class Feat184StateBlock2(val state: Feat184UiModel, val checksum: Int)
data class Feat184StateBlock3(val state: Feat184UiModel, val checksum: Int)
data class Feat184StateBlock4(val state: Feat184UiModel, val checksum: Int)
data class Feat184StateBlock5(val state: Feat184UiModel, val checksum: Int)
data class Feat184StateBlock6(val state: Feat184UiModel, val checksum: Int)
data class Feat184StateBlock7(val state: Feat184UiModel, val checksum: Int)
data class Feat184StateBlock8(val state: Feat184UiModel, val checksum: Int)
data class Feat184StateBlock9(val state: Feat184UiModel, val checksum: Int)
data class Feat184StateBlock10(val state: Feat184UiModel, val checksum: Int)

fun buildFeat184UserItem(user: CoreUser, index: Int): Feat184UserItem1 {
    return Feat184UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat184StateBlock(model: Feat184UiModel): Feat184StateBlock1 {
    return Feat184StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat184UserSummary> {
    val list = java.util.ArrayList<Feat184UserSummary>(users.size)
    for (user in users) {
        list += Feat184UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat184UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat184UiModel {
    val summaries = (0 until count).map {
        Feat184UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat184UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat184UiModel> {
    val models = java.util.ArrayList<Feat184UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat184AnalyticsEvent1(val name: String, val value: String)
data class Feat184AnalyticsEvent2(val name: String, val value: String)
data class Feat184AnalyticsEvent3(val name: String, val value: String)
data class Feat184AnalyticsEvent4(val name: String, val value: String)
data class Feat184AnalyticsEvent5(val name: String, val value: String)
data class Feat184AnalyticsEvent6(val name: String, val value: String)
data class Feat184AnalyticsEvent7(val name: String, val value: String)
data class Feat184AnalyticsEvent8(val name: String, val value: String)
data class Feat184AnalyticsEvent9(val name: String, val value: String)
data class Feat184AnalyticsEvent10(val name: String, val value: String)

fun logFeat184Event1(event: Feat184AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat184Event2(event: Feat184AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat184Event3(event: Feat184AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat184Event4(event: Feat184AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat184Event5(event: Feat184AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat184Event6(event: Feat184AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat184Event7(event: Feat184AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat184Event8(event: Feat184AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat184Event9(event: Feat184AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat184Event10(event: Feat184AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat184Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat184Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat184Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat184Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat184Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat184Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat184Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat184Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat184Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat184Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat184(u: CoreUser): Feat184Projection1 =
    Feat184Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat184Projection1> {
    val list = java.util.ArrayList<Feat184Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat184(u)
    }
    return list
}
