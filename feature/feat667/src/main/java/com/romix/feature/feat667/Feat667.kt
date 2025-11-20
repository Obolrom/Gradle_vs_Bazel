package com.romix.feature.feat667

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat667Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat667UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat667FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat667UserSummary
)

data class Feat667UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat667NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat667Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat667Config = Feat667Config()
) {

    fun loadSnapshot(userId: Long): Feat667NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat667NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat667UserSummary {
        return Feat667UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat667FeedItem> {
        val result = java.util.ArrayList<Feat667FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat667FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat667UiMapper {

    fun mapToUi(model: List<Feat667FeedItem>): Feat667UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat667UiModel(
            header = UiText("Feat667 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat667UiModel =
        Feat667UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat667UiModel =
        Feat667UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat667UiModel =
        Feat667UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat667Service(
    private val repository: Feat667Repository,
    private val uiMapper: Feat667UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat667UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat667UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat667UserItem1(val user: CoreUser, val label: String)
data class Feat667UserItem2(val user: CoreUser, val label: String)
data class Feat667UserItem3(val user: CoreUser, val label: String)
data class Feat667UserItem4(val user: CoreUser, val label: String)
data class Feat667UserItem5(val user: CoreUser, val label: String)
data class Feat667UserItem6(val user: CoreUser, val label: String)
data class Feat667UserItem7(val user: CoreUser, val label: String)
data class Feat667UserItem8(val user: CoreUser, val label: String)
data class Feat667UserItem9(val user: CoreUser, val label: String)
data class Feat667UserItem10(val user: CoreUser, val label: String)

data class Feat667StateBlock1(val state: Feat667UiModel, val checksum: Int)
data class Feat667StateBlock2(val state: Feat667UiModel, val checksum: Int)
data class Feat667StateBlock3(val state: Feat667UiModel, val checksum: Int)
data class Feat667StateBlock4(val state: Feat667UiModel, val checksum: Int)
data class Feat667StateBlock5(val state: Feat667UiModel, val checksum: Int)
data class Feat667StateBlock6(val state: Feat667UiModel, val checksum: Int)
data class Feat667StateBlock7(val state: Feat667UiModel, val checksum: Int)
data class Feat667StateBlock8(val state: Feat667UiModel, val checksum: Int)
data class Feat667StateBlock9(val state: Feat667UiModel, val checksum: Int)
data class Feat667StateBlock10(val state: Feat667UiModel, val checksum: Int)

fun buildFeat667UserItem(user: CoreUser, index: Int): Feat667UserItem1 {
    return Feat667UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat667StateBlock(model: Feat667UiModel): Feat667StateBlock1 {
    return Feat667StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat667UserSummary> {
    val list = java.util.ArrayList<Feat667UserSummary>(users.size)
    for (user in users) {
        list += Feat667UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat667UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat667UiModel {
    val summaries = (0 until count).map {
        Feat667UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat667UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat667UiModel> {
    val models = java.util.ArrayList<Feat667UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat667AnalyticsEvent1(val name: String, val value: String)
data class Feat667AnalyticsEvent2(val name: String, val value: String)
data class Feat667AnalyticsEvent3(val name: String, val value: String)
data class Feat667AnalyticsEvent4(val name: String, val value: String)
data class Feat667AnalyticsEvent5(val name: String, val value: String)
data class Feat667AnalyticsEvent6(val name: String, val value: String)
data class Feat667AnalyticsEvent7(val name: String, val value: String)
data class Feat667AnalyticsEvent8(val name: String, val value: String)
data class Feat667AnalyticsEvent9(val name: String, val value: String)
data class Feat667AnalyticsEvent10(val name: String, val value: String)

fun logFeat667Event1(event: Feat667AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat667Event2(event: Feat667AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat667Event3(event: Feat667AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat667Event4(event: Feat667AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat667Event5(event: Feat667AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat667Event6(event: Feat667AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat667Event7(event: Feat667AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat667Event8(event: Feat667AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat667Event9(event: Feat667AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat667Event10(event: Feat667AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat667Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat667Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat667Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat667Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat667Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat667Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat667Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat667Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat667Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat667Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat667(u: CoreUser): Feat667Projection1 =
    Feat667Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat667Projection1> {
    val list = java.util.ArrayList<Feat667Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat667(u)
    }
    return list
}
