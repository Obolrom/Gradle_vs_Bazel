package com.romix.feature.feat592

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat592Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat592UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat592FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat592UserSummary
)

data class Feat592UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat592NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat592Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat592Config = Feat592Config()
) {

    fun loadSnapshot(userId: Long): Feat592NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat592NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat592UserSummary {
        return Feat592UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat592FeedItem> {
        val result = java.util.ArrayList<Feat592FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat592FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat592UiMapper {

    fun mapToUi(model: List<Feat592FeedItem>): Feat592UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat592UiModel(
            header = UiText("Feat592 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat592UiModel =
        Feat592UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat592UiModel =
        Feat592UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat592UiModel =
        Feat592UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat592Service(
    private val repository: Feat592Repository,
    private val uiMapper: Feat592UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat592UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat592UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat592UserItem1(val user: CoreUser, val label: String)
data class Feat592UserItem2(val user: CoreUser, val label: String)
data class Feat592UserItem3(val user: CoreUser, val label: String)
data class Feat592UserItem4(val user: CoreUser, val label: String)
data class Feat592UserItem5(val user: CoreUser, val label: String)
data class Feat592UserItem6(val user: CoreUser, val label: String)
data class Feat592UserItem7(val user: CoreUser, val label: String)
data class Feat592UserItem8(val user: CoreUser, val label: String)
data class Feat592UserItem9(val user: CoreUser, val label: String)
data class Feat592UserItem10(val user: CoreUser, val label: String)

data class Feat592StateBlock1(val state: Feat592UiModel, val checksum: Int)
data class Feat592StateBlock2(val state: Feat592UiModel, val checksum: Int)
data class Feat592StateBlock3(val state: Feat592UiModel, val checksum: Int)
data class Feat592StateBlock4(val state: Feat592UiModel, val checksum: Int)
data class Feat592StateBlock5(val state: Feat592UiModel, val checksum: Int)
data class Feat592StateBlock6(val state: Feat592UiModel, val checksum: Int)
data class Feat592StateBlock7(val state: Feat592UiModel, val checksum: Int)
data class Feat592StateBlock8(val state: Feat592UiModel, val checksum: Int)
data class Feat592StateBlock9(val state: Feat592UiModel, val checksum: Int)
data class Feat592StateBlock10(val state: Feat592UiModel, val checksum: Int)

fun buildFeat592UserItem(user: CoreUser, index: Int): Feat592UserItem1 {
    return Feat592UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat592StateBlock(model: Feat592UiModel): Feat592StateBlock1 {
    return Feat592StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat592UserSummary> {
    val list = java.util.ArrayList<Feat592UserSummary>(users.size)
    for (user in users) {
        list += Feat592UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat592UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat592UiModel {
    val summaries = (0 until count).map {
        Feat592UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat592UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat592UiModel> {
    val models = java.util.ArrayList<Feat592UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat592AnalyticsEvent1(val name: String, val value: String)
data class Feat592AnalyticsEvent2(val name: String, val value: String)
data class Feat592AnalyticsEvent3(val name: String, val value: String)
data class Feat592AnalyticsEvent4(val name: String, val value: String)
data class Feat592AnalyticsEvent5(val name: String, val value: String)
data class Feat592AnalyticsEvent6(val name: String, val value: String)
data class Feat592AnalyticsEvent7(val name: String, val value: String)
data class Feat592AnalyticsEvent8(val name: String, val value: String)
data class Feat592AnalyticsEvent9(val name: String, val value: String)
data class Feat592AnalyticsEvent10(val name: String, val value: String)

fun logFeat592Event1(event: Feat592AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat592Event2(event: Feat592AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat592Event3(event: Feat592AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat592Event4(event: Feat592AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat592Event5(event: Feat592AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat592Event6(event: Feat592AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat592Event7(event: Feat592AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat592Event8(event: Feat592AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat592Event9(event: Feat592AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat592Event10(event: Feat592AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat592Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat592Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat592Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat592Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat592Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat592Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat592Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat592Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat592Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat592Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat592(u: CoreUser): Feat592Projection1 =
    Feat592Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat592Projection1> {
    val list = java.util.ArrayList<Feat592Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat592(u)
    }
    return list
}
