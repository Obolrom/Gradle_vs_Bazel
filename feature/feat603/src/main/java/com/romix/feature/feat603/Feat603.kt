package com.romix.feature.feat603

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat603Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat603UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat603FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat603UserSummary
)

data class Feat603UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat603NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat603Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat603Config = Feat603Config()
) {

    fun loadSnapshot(userId: Long): Feat603NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat603NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat603UserSummary {
        return Feat603UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat603FeedItem> {
        val result = java.util.ArrayList<Feat603FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat603FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat603UiMapper {

    fun mapToUi(model: List<Feat603FeedItem>): Feat603UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat603UiModel(
            header = UiText("Feat603 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat603UiModel =
        Feat603UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat603UiModel =
        Feat603UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat603UiModel =
        Feat603UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat603Service(
    private val repository: Feat603Repository,
    private val uiMapper: Feat603UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat603UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat603UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat603UserItem1(val user: CoreUser, val label: String)
data class Feat603UserItem2(val user: CoreUser, val label: String)
data class Feat603UserItem3(val user: CoreUser, val label: String)
data class Feat603UserItem4(val user: CoreUser, val label: String)
data class Feat603UserItem5(val user: CoreUser, val label: String)
data class Feat603UserItem6(val user: CoreUser, val label: String)
data class Feat603UserItem7(val user: CoreUser, val label: String)
data class Feat603UserItem8(val user: CoreUser, val label: String)
data class Feat603UserItem9(val user: CoreUser, val label: String)
data class Feat603UserItem10(val user: CoreUser, val label: String)

data class Feat603StateBlock1(val state: Feat603UiModel, val checksum: Int)
data class Feat603StateBlock2(val state: Feat603UiModel, val checksum: Int)
data class Feat603StateBlock3(val state: Feat603UiModel, val checksum: Int)
data class Feat603StateBlock4(val state: Feat603UiModel, val checksum: Int)
data class Feat603StateBlock5(val state: Feat603UiModel, val checksum: Int)
data class Feat603StateBlock6(val state: Feat603UiModel, val checksum: Int)
data class Feat603StateBlock7(val state: Feat603UiModel, val checksum: Int)
data class Feat603StateBlock8(val state: Feat603UiModel, val checksum: Int)
data class Feat603StateBlock9(val state: Feat603UiModel, val checksum: Int)
data class Feat603StateBlock10(val state: Feat603UiModel, val checksum: Int)

fun buildFeat603UserItem(user: CoreUser, index: Int): Feat603UserItem1 {
    return Feat603UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat603StateBlock(model: Feat603UiModel): Feat603StateBlock1 {
    return Feat603StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat603UserSummary> {
    val list = java.util.ArrayList<Feat603UserSummary>(users.size)
    for (user in users) {
        list += Feat603UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat603UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat603UiModel {
    val summaries = (0 until count).map {
        Feat603UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat603UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat603UiModel> {
    val models = java.util.ArrayList<Feat603UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat603AnalyticsEvent1(val name: String, val value: String)
data class Feat603AnalyticsEvent2(val name: String, val value: String)
data class Feat603AnalyticsEvent3(val name: String, val value: String)
data class Feat603AnalyticsEvent4(val name: String, val value: String)
data class Feat603AnalyticsEvent5(val name: String, val value: String)
data class Feat603AnalyticsEvent6(val name: String, val value: String)
data class Feat603AnalyticsEvent7(val name: String, val value: String)
data class Feat603AnalyticsEvent8(val name: String, val value: String)
data class Feat603AnalyticsEvent9(val name: String, val value: String)
data class Feat603AnalyticsEvent10(val name: String, val value: String)

fun logFeat603Event1(event: Feat603AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat603Event2(event: Feat603AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat603Event3(event: Feat603AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat603Event4(event: Feat603AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat603Event5(event: Feat603AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat603Event6(event: Feat603AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat603Event7(event: Feat603AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat603Event8(event: Feat603AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat603Event9(event: Feat603AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat603Event10(event: Feat603AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat603Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat603Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat603Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat603Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat603Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat603Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat603Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat603Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat603Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat603Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat603(u: CoreUser): Feat603Projection1 =
    Feat603Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat603Projection1> {
    val list = java.util.ArrayList<Feat603Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat603(u)
    }
    return list
}
