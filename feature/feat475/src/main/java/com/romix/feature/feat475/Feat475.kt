package com.romix.feature.feat475

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat475Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat475UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat475FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat475UserSummary
)

data class Feat475UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat475NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat475Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat475Config = Feat475Config()
) {

    fun loadSnapshot(userId: Long): Feat475NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat475NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat475UserSummary {
        return Feat475UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat475FeedItem> {
        val result = java.util.ArrayList<Feat475FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat475FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat475UiMapper {

    fun mapToUi(model: List<Feat475FeedItem>): Feat475UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat475UiModel(
            header = UiText("Feat475 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat475UiModel =
        Feat475UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat475UiModel =
        Feat475UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat475UiModel =
        Feat475UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat475Service(
    private val repository: Feat475Repository,
    private val uiMapper: Feat475UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat475UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat475UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat475UserItem1(val user: CoreUser, val label: String)
data class Feat475UserItem2(val user: CoreUser, val label: String)
data class Feat475UserItem3(val user: CoreUser, val label: String)
data class Feat475UserItem4(val user: CoreUser, val label: String)
data class Feat475UserItem5(val user: CoreUser, val label: String)
data class Feat475UserItem6(val user: CoreUser, val label: String)
data class Feat475UserItem7(val user: CoreUser, val label: String)
data class Feat475UserItem8(val user: CoreUser, val label: String)
data class Feat475UserItem9(val user: CoreUser, val label: String)
data class Feat475UserItem10(val user: CoreUser, val label: String)

data class Feat475StateBlock1(val state: Feat475UiModel, val checksum: Int)
data class Feat475StateBlock2(val state: Feat475UiModel, val checksum: Int)
data class Feat475StateBlock3(val state: Feat475UiModel, val checksum: Int)
data class Feat475StateBlock4(val state: Feat475UiModel, val checksum: Int)
data class Feat475StateBlock5(val state: Feat475UiModel, val checksum: Int)
data class Feat475StateBlock6(val state: Feat475UiModel, val checksum: Int)
data class Feat475StateBlock7(val state: Feat475UiModel, val checksum: Int)
data class Feat475StateBlock8(val state: Feat475UiModel, val checksum: Int)
data class Feat475StateBlock9(val state: Feat475UiModel, val checksum: Int)
data class Feat475StateBlock10(val state: Feat475UiModel, val checksum: Int)

fun buildFeat475UserItem(user: CoreUser, index: Int): Feat475UserItem1 {
    return Feat475UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat475StateBlock(model: Feat475UiModel): Feat475StateBlock1 {
    return Feat475StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat475UserSummary> {
    val list = java.util.ArrayList<Feat475UserSummary>(users.size)
    for (user in users) {
        list += Feat475UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat475UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat475UiModel {
    val summaries = (0 until count).map {
        Feat475UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat475UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat475UiModel> {
    val models = java.util.ArrayList<Feat475UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat475AnalyticsEvent1(val name: String, val value: String)
data class Feat475AnalyticsEvent2(val name: String, val value: String)
data class Feat475AnalyticsEvent3(val name: String, val value: String)
data class Feat475AnalyticsEvent4(val name: String, val value: String)
data class Feat475AnalyticsEvent5(val name: String, val value: String)
data class Feat475AnalyticsEvent6(val name: String, val value: String)
data class Feat475AnalyticsEvent7(val name: String, val value: String)
data class Feat475AnalyticsEvent8(val name: String, val value: String)
data class Feat475AnalyticsEvent9(val name: String, val value: String)
data class Feat475AnalyticsEvent10(val name: String, val value: String)

fun logFeat475Event1(event: Feat475AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat475Event2(event: Feat475AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat475Event3(event: Feat475AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat475Event4(event: Feat475AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat475Event5(event: Feat475AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat475Event6(event: Feat475AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat475Event7(event: Feat475AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat475Event8(event: Feat475AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat475Event9(event: Feat475AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat475Event10(event: Feat475AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat475Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat475Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat475Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat475Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat475Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat475Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat475Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat475Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat475Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat475Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat475(u: CoreUser): Feat475Projection1 =
    Feat475Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat475Projection1> {
    val list = java.util.ArrayList<Feat475Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat475(u)
    }
    return list
}
