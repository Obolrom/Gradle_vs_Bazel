package com.romix.feature.feat693

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat693Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat693UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat693FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat693UserSummary
)

data class Feat693UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat693NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat693Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat693Config = Feat693Config()
) {

    fun loadSnapshot(userId: Long): Feat693NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat693NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat693UserSummary {
        return Feat693UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat693FeedItem> {
        val result = java.util.ArrayList<Feat693FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat693FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat693UiMapper {

    fun mapToUi(model: List<Feat693FeedItem>): Feat693UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat693UiModel(
            header = UiText("Feat693 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat693UiModel =
        Feat693UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat693UiModel =
        Feat693UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat693UiModel =
        Feat693UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat693Service(
    private val repository: Feat693Repository,
    private val uiMapper: Feat693UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat693UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat693UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat693UserItem1(val user: CoreUser, val label: String)
data class Feat693UserItem2(val user: CoreUser, val label: String)
data class Feat693UserItem3(val user: CoreUser, val label: String)
data class Feat693UserItem4(val user: CoreUser, val label: String)
data class Feat693UserItem5(val user: CoreUser, val label: String)
data class Feat693UserItem6(val user: CoreUser, val label: String)
data class Feat693UserItem7(val user: CoreUser, val label: String)
data class Feat693UserItem8(val user: CoreUser, val label: String)
data class Feat693UserItem9(val user: CoreUser, val label: String)
data class Feat693UserItem10(val user: CoreUser, val label: String)

data class Feat693StateBlock1(val state: Feat693UiModel, val checksum: Int)
data class Feat693StateBlock2(val state: Feat693UiModel, val checksum: Int)
data class Feat693StateBlock3(val state: Feat693UiModel, val checksum: Int)
data class Feat693StateBlock4(val state: Feat693UiModel, val checksum: Int)
data class Feat693StateBlock5(val state: Feat693UiModel, val checksum: Int)
data class Feat693StateBlock6(val state: Feat693UiModel, val checksum: Int)
data class Feat693StateBlock7(val state: Feat693UiModel, val checksum: Int)
data class Feat693StateBlock8(val state: Feat693UiModel, val checksum: Int)
data class Feat693StateBlock9(val state: Feat693UiModel, val checksum: Int)
data class Feat693StateBlock10(val state: Feat693UiModel, val checksum: Int)

fun buildFeat693UserItem(user: CoreUser, index: Int): Feat693UserItem1 {
    return Feat693UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat693StateBlock(model: Feat693UiModel): Feat693StateBlock1 {
    return Feat693StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat693UserSummary> {
    val list = java.util.ArrayList<Feat693UserSummary>(users.size)
    for (user in users) {
        list += Feat693UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat693UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat693UiModel {
    val summaries = (0 until count).map {
        Feat693UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat693UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat693UiModel> {
    val models = java.util.ArrayList<Feat693UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat693AnalyticsEvent1(val name: String, val value: String)
data class Feat693AnalyticsEvent2(val name: String, val value: String)
data class Feat693AnalyticsEvent3(val name: String, val value: String)
data class Feat693AnalyticsEvent4(val name: String, val value: String)
data class Feat693AnalyticsEvent5(val name: String, val value: String)
data class Feat693AnalyticsEvent6(val name: String, val value: String)
data class Feat693AnalyticsEvent7(val name: String, val value: String)
data class Feat693AnalyticsEvent8(val name: String, val value: String)
data class Feat693AnalyticsEvent9(val name: String, val value: String)
data class Feat693AnalyticsEvent10(val name: String, val value: String)

fun logFeat693Event1(event: Feat693AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat693Event2(event: Feat693AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat693Event3(event: Feat693AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat693Event4(event: Feat693AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat693Event5(event: Feat693AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat693Event6(event: Feat693AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat693Event7(event: Feat693AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat693Event8(event: Feat693AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat693Event9(event: Feat693AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat693Event10(event: Feat693AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat693Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat693Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat693Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat693Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat693Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat693Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat693Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat693Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat693Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat693Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat693(u: CoreUser): Feat693Projection1 =
    Feat693Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat693Projection1> {
    val list = java.util.ArrayList<Feat693Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat693(u)
    }
    return list
}
