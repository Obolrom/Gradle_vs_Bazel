package com.romix.feature.feat644

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat644Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat644UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat644FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat644UserSummary
)

data class Feat644UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat644NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat644Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat644Config = Feat644Config()
) {

    fun loadSnapshot(userId: Long): Feat644NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat644NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat644UserSummary {
        return Feat644UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat644FeedItem> {
        val result = java.util.ArrayList<Feat644FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat644FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat644UiMapper {

    fun mapToUi(model: List<Feat644FeedItem>): Feat644UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat644UiModel(
            header = UiText("Feat644 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat644UiModel =
        Feat644UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat644UiModel =
        Feat644UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat644UiModel =
        Feat644UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat644Service(
    private val repository: Feat644Repository,
    private val uiMapper: Feat644UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat644UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat644UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat644UserItem1(val user: CoreUser, val label: String)
data class Feat644UserItem2(val user: CoreUser, val label: String)
data class Feat644UserItem3(val user: CoreUser, val label: String)
data class Feat644UserItem4(val user: CoreUser, val label: String)
data class Feat644UserItem5(val user: CoreUser, val label: String)
data class Feat644UserItem6(val user: CoreUser, val label: String)
data class Feat644UserItem7(val user: CoreUser, val label: String)
data class Feat644UserItem8(val user: CoreUser, val label: String)
data class Feat644UserItem9(val user: CoreUser, val label: String)
data class Feat644UserItem10(val user: CoreUser, val label: String)

data class Feat644StateBlock1(val state: Feat644UiModel, val checksum: Int)
data class Feat644StateBlock2(val state: Feat644UiModel, val checksum: Int)
data class Feat644StateBlock3(val state: Feat644UiModel, val checksum: Int)
data class Feat644StateBlock4(val state: Feat644UiModel, val checksum: Int)
data class Feat644StateBlock5(val state: Feat644UiModel, val checksum: Int)
data class Feat644StateBlock6(val state: Feat644UiModel, val checksum: Int)
data class Feat644StateBlock7(val state: Feat644UiModel, val checksum: Int)
data class Feat644StateBlock8(val state: Feat644UiModel, val checksum: Int)
data class Feat644StateBlock9(val state: Feat644UiModel, val checksum: Int)
data class Feat644StateBlock10(val state: Feat644UiModel, val checksum: Int)

fun buildFeat644UserItem(user: CoreUser, index: Int): Feat644UserItem1 {
    return Feat644UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat644StateBlock(model: Feat644UiModel): Feat644StateBlock1 {
    return Feat644StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat644UserSummary> {
    val list = java.util.ArrayList<Feat644UserSummary>(users.size)
    for (user in users) {
        list += Feat644UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat644UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat644UiModel {
    val summaries = (0 until count).map {
        Feat644UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat644UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat644UiModel> {
    val models = java.util.ArrayList<Feat644UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat644AnalyticsEvent1(val name: String, val value: String)
data class Feat644AnalyticsEvent2(val name: String, val value: String)
data class Feat644AnalyticsEvent3(val name: String, val value: String)
data class Feat644AnalyticsEvent4(val name: String, val value: String)
data class Feat644AnalyticsEvent5(val name: String, val value: String)
data class Feat644AnalyticsEvent6(val name: String, val value: String)
data class Feat644AnalyticsEvent7(val name: String, val value: String)
data class Feat644AnalyticsEvent8(val name: String, val value: String)
data class Feat644AnalyticsEvent9(val name: String, val value: String)
data class Feat644AnalyticsEvent10(val name: String, val value: String)

fun logFeat644Event1(event: Feat644AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat644Event2(event: Feat644AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat644Event3(event: Feat644AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat644Event4(event: Feat644AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat644Event5(event: Feat644AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat644Event6(event: Feat644AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat644Event7(event: Feat644AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat644Event8(event: Feat644AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat644Event9(event: Feat644AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat644Event10(event: Feat644AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat644Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat644Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat644Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat644Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat644Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat644Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat644Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat644Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat644Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat644Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat644(u: CoreUser): Feat644Projection1 =
    Feat644Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat644Projection1> {
    val list = java.util.ArrayList<Feat644Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat644(u)
    }
    return list
}
