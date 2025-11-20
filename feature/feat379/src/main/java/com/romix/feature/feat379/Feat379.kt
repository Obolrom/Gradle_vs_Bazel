package com.romix.feature.feat379

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat379Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat379UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat379FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat379UserSummary
)

data class Feat379UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat379NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat379Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat379Config = Feat379Config()
) {

    fun loadSnapshot(userId: Long): Feat379NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat379NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat379UserSummary {
        return Feat379UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat379FeedItem> {
        val result = java.util.ArrayList<Feat379FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat379FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat379UiMapper {

    fun mapToUi(model: List<Feat379FeedItem>): Feat379UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat379UiModel(
            header = UiText("Feat379 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat379UiModel =
        Feat379UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat379UiModel =
        Feat379UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat379UiModel =
        Feat379UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat379Service(
    private val repository: Feat379Repository,
    private val uiMapper: Feat379UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat379UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat379UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat379UserItem1(val user: CoreUser, val label: String)
data class Feat379UserItem2(val user: CoreUser, val label: String)
data class Feat379UserItem3(val user: CoreUser, val label: String)
data class Feat379UserItem4(val user: CoreUser, val label: String)
data class Feat379UserItem5(val user: CoreUser, val label: String)
data class Feat379UserItem6(val user: CoreUser, val label: String)
data class Feat379UserItem7(val user: CoreUser, val label: String)
data class Feat379UserItem8(val user: CoreUser, val label: String)
data class Feat379UserItem9(val user: CoreUser, val label: String)
data class Feat379UserItem10(val user: CoreUser, val label: String)

data class Feat379StateBlock1(val state: Feat379UiModel, val checksum: Int)
data class Feat379StateBlock2(val state: Feat379UiModel, val checksum: Int)
data class Feat379StateBlock3(val state: Feat379UiModel, val checksum: Int)
data class Feat379StateBlock4(val state: Feat379UiModel, val checksum: Int)
data class Feat379StateBlock5(val state: Feat379UiModel, val checksum: Int)
data class Feat379StateBlock6(val state: Feat379UiModel, val checksum: Int)
data class Feat379StateBlock7(val state: Feat379UiModel, val checksum: Int)
data class Feat379StateBlock8(val state: Feat379UiModel, val checksum: Int)
data class Feat379StateBlock9(val state: Feat379UiModel, val checksum: Int)
data class Feat379StateBlock10(val state: Feat379UiModel, val checksum: Int)

fun buildFeat379UserItem(user: CoreUser, index: Int): Feat379UserItem1 {
    return Feat379UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat379StateBlock(model: Feat379UiModel): Feat379StateBlock1 {
    return Feat379StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat379UserSummary> {
    val list = java.util.ArrayList<Feat379UserSummary>(users.size)
    for (user in users) {
        list += Feat379UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat379UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat379UiModel {
    val summaries = (0 until count).map {
        Feat379UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat379UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat379UiModel> {
    val models = java.util.ArrayList<Feat379UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat379AnalyticsEvent1(val name: String, val value: String)
data class Feat379AnalyticsEvent2(val name: String, val value: String)
data class Feat379AnalyticsEvent3(val name: String, val value: String)
data class Feat379AnalyticsEvent4(val name: String, val value: String)
data class Feat379AnalyticsEvent5(val name: String, val value: String)
data class Feat379AnalyticsEvent6(val name: String, val value: String)
data class Feat379AnalyticsEvent7(val name: String, val value: String)
data class Feat379AnalyticsEvent8(val name: String, val value: String)
data class Feat379AnalyticsEvent9(val name: String, val value: String)
data class Feat379AnalyticsEvent10(val name: String, val value: String)

fun logFeat379Event1(event: Feat379AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat379Event2(event: Feat379AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat379Event3(event: Feat379AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat379Event4(event: Feat379AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat379Event5(event: Feat379AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat379Event6(event: Feat379AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat379Event7(event: Feat379AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat379Event8(event: Feat379AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat379Event9(event: Feat379AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat379Event10(event: Feat379AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat379Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat379Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat379Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat379Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat379Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat379Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat379Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat379Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat379Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat379Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat379(u: CoreUser): Feat379Projection1 =
    Feat379Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat379Projection1> {
    val list = java.util.ArrayList<Feat379Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat379(u)
    }
    return list
}
