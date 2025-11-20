package com.romix.feature.feat111

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat111Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat111UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat111FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat111UserSummary
)

data class Feat111UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat111NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat111Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat111Config = Feat111Config()
) {

    fun loadSnapshot(userId: Long): Feat111NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat111NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat111UserSummary {
        return Feat111UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat111FeedItem> {
        val result = java.util.ArrayList<Feat111FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat111FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat111UiMapper {

    fun mapToUi(model: List<Feat111FeedItem>): Feat111UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat111UiModel(
            header = UiText("Feat111 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat111UiModel =
        Feat111UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat111UiModel =
        Feat111UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat111UiModel =
        Feat111UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat111Service(
    private val repository: Feat111Repository,
    private val uiMapper: Feat111UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat111UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat111UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat111UserItem1(val user: CoreUser, val label: String)
data class Feat111UserItem2(val user: CoreUser, val label: String)
data class Feat111UserItem3(val user: CoreUser, val label: String)
data class Feat111UserItem4(val user: CoreUser, val label: String)
data class Feat111UserItem5(val user: CoreUser, val label: String)
data class Feat111UserItem6(val user: CoreUser, val label: String)
data class Feat111UserItem7(val user: CoreUser, val label: String)
data class Feat111UserItem8(val user: CoreUser, val label: String)
data class Feat111UserItem9(val user: CoreUser, val label: String)
data class Feat111UserItem10(val user: CoreUser, val label: String)

data class Feat111StateBlock1(val state: Feat111UiModel, val checksum: Int)
data class Feat111StateBlock2(val state: Feat111UiModel, val checksum: Int)
data class Feat111StateBlock3(val state: Feat111UiModel, val checksum: Int)
data class Feat111StateBlock4(val state: Feat111UiModel, val checksum: Int)
data class Feat111StateBlock5(val state: Feat111UiModel, val checksum: Int)
data class Feat111StateBlock6(val state: Feat111UiModel, val checksum: Int)
data class Feat111StateBlock7(val state: Feat111UiModel, val checksum: Int)
data class Feat111StateBlock8(val state: Feat111UiModel, val checksum: Int)
data class Feat111StateBlock9(val state: Feat111UiModel, val checksum: Int)
data class Feat111StateBlock10(val state: Feat111UiModel, val checksum: Int)

fun buildFeat111UserItem(user: CoreUser, index: Int): Feat111UserItem1 {
    return Feat111UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat111StateBlock(model: Feat111UiModel): Feat111StateBlock1 {
    return Feat111StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat111UserSummary> {
    val list = java.util.ArrayList<Feat111UserSummary>(users.size)
    for (user in users) {
        list += Feat111UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat111UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat111UiModel {
    val summaries = (0 until count).map {
        Feat111UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat111UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat111UiModel> {
    val models = java.util.ArrayList<Feat111UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat111AnalyticsEvent1(val name: String, val value: String)
data class Feat111AnalyticsEvent2(val name: String, val value: String)
data class Feat111AnalyticsEvent3(val name: String, val value: String)
data class Feat111AnalyticsEvent4(val name: String, val value: String)
data class Feat111AnalyticsEvent5(val name: String, val value: String)
data class Feat111AnalyticsEvent6(val name: String, val value: String)
data class Feat111AnalyticsEvent7(val name: String, val value: String)
data class Feat111AnalyticsEvent8(val name: String, val value: String)
data class Feat111AnalyticsEvent9(val name: String, val value: String)
data class Feat111AnalyticsEvent10(val name: String, val value: String)

fun logFeat111Event1(event: Feat111AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat111Event2(event: Feat111AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat111Event3(event: Feat111AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat111Event4(event: Feat111AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat111Event5(event: Feat111AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat111Event6(event: Feat111AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat111Event7(event: Feat111AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat111Event8(event: Feat111AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat111Event9(event: Feat111AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat111Event10(event: Feat111AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat111Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat111Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat111Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat111Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat111Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat111Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat111Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat111Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat111Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat111Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat111(u: CoreUser): Feat111Projection1 =
    Feat111Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat111Projection1> {
    val list = java.util.ArrayList<Feat111Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat111(u)
    }
    return list
}
