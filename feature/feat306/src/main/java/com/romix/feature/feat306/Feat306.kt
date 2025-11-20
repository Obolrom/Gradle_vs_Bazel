package com.romix.feature.feat306

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat306Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat306UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat306FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat306UserSummary
)

data class Feat306UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat306NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat306Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat306Config = Feat306Config()
) {

    fun loadSnapshot(userId: Long): Feat306NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat306NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat306UserSummary {
        return Feat306UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat306FeedItem> {
        val result = java.util.ArrayList<Feat306FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat306FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat306UiMapper {

    fun mapToUi(model: List<Feat306FeedItem>): Feat306UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat306UiModel(
            header = UiText("Feat306 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat306UiModel =
        Feat306UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat306UiModel =
        Feat306UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat306UiModel =
        Feat306UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat306Service(
    private val repository: Feat306Repository,
    private val uiMapper: Feat306UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat306UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat306UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat306UserItem1(val user: CoreUser, val label: String)
data class Feat306UserItem2(val user: CoreUser, val label: String)
data class Feat306UserItem3(val user: CoreUser, val label: String)
data class Feat306UserItem4(val user: CoreUser, val label: String)
data class Feat306UserItem5(val user: CoreUser, val label: String)
data class Feat306UserItem6(val user: CoreUser, val label: String)
data class Feat306UserItem7(val user: CoreUser, val label: String)
data class Feat306UserItem8(val user: CoreUser, val label: String)
data class Feat306UserItem9(val user: CoreUser, val label: String)
data class Feat306UserItem10(val user: CoreUser, val label: String)

data class Feat306StateBlock1(val state: Feat306UiModel, val checksum: Int)
data class Feat306StateBlock2(val state: Feat306UiModel, val checksum: Int)
data class Feat306StateBlock3(val state: Feat306UiModel, val checksum: Int)
data class Feat306StateBlock4(val state: Feat306UiModel, val checksum: Int)
data class Feat306StateBlock5(val state: Feat306UiModel, val checksum: Int)
data class Feat306StateBlock6(val state: Feat306UiModel, val checksum: Int)
data class Feat306StateBlock7(val state: Feat306UiModel, val checksum: Int)
data class Feat306StateBlock8(val state: Feat306UiModel, val checksum: Int)
data class Feat306StateBlock9(val state: Feat306UiModel, val checksum: Int)
data class Feat306StateBlock10(val state: Feat306UiModel, val checksum: Int)

fun buildFeat306UserItem(user: CoreUser, index: Int): Feat306UserItem1 {
    return Feat306UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat306StateBlock(model: Feat306UiModel): Feat306StateBlock1 {
    return Feat306StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat306UserSummary> {
    val list = java.util.ArrayList<Feat306UserSummary>(users.size)
    for (user in users) {
        list += Feat306UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat306UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat306UiModel {
    val summaries = (0 until count).map {
        Feat306UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat306UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat306UiModel> {
    val models = java.util.ArrayList<Feat306UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat306AnalyticsEvent1(val name: String, val value: String)
data class Feat306AnalyticsEvent2(val name: String, val value: String)
data class Feat306AnalyticsEvent3(val name: String, val value: String)
data class Feat306AnalyticsEvent4(val name: String, val value: String)
data class Feat306AnalyticsEvent5(val name: String, val value: String)
data class Feat306AnalyticsEvent6(val name: String, val value: String)
data class Feat306AnalyticsEvent7(val name: String, val value: String)
data class Feat306AnalyticsEvent8(val name: String, val value: String)
data class Feat306AnalyticsEvent9(val name: String, val value: String)
data class Feat306AnalyticsEvent10(val name: String, val value: String)

fun logFeat306Event1(event: Feat306AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat306Event2(event: Feat306AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat306Event3(event: Feat306AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat306Event4(event: Feat306AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat306Event5(event: Feat306AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat306Event6(event: Feat306AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat306Event7(event: Feat306AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat306Event8(event: Feat306AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat306Event9(event: Feat306AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat306Event10(event: Feat306AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat306Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat306Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat306Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat306Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat306Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat306Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat306Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat306Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat306Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat306Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat306(u: CoreUser): Feat306Projection1 =
    Feat306Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat306Projection1> {
    val list = java.util.ArrayList<Feat306Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat306(u)
    }
    return list
}
